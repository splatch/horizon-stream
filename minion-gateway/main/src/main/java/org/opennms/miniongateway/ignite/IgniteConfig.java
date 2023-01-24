package org.opennms.miniongateway.ignite;

import javax.sql.DataSource;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.IgniteSpring;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.store.jdbc.CacheJdbcBlobStore;
import org.apache.ignite.cache.store.jdbc.CacheJdbcBlobStoreFactory;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.ClientConnectorConfiguration;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.configuration.ThinClientConfiguration;
import org.apache.ignite.kubernetes.configuration.KubernetesConnectionConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.kubernetes.TcpDiscoveryKubernetesIpFinder;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;
import org.opennms.miniongateway.grpc.twin.AbstractTwinPublisher;
import org.opennms.miniongateway.grpc.twin.AbstractTwinPublisher.SessionKey;
import org.opennms.miniongateway.grpc.twin.TwinUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class IgniteConfig {

    public static final String DATA_SOURCE_BEAN_NAME = "dataSource";
    @Value("${ignite.use-kubernetes:false}")
    private boolean useKubernetes;

    @Value("${ignite.kubernetes-service-name:unknown}")
    private String kubernetesServiceName;

    @Autowired
    private ApplicationContext applicationContext;

//========================================
// Beans
//----------------------------------------

    @Bean
    public CacheJdbcBlobStoreFactory createCacheJdbcBlobStoreFactory(@Qualifier(DATA_SOURCE_BEAN_NAME) DataSource dataSource) {
        return new CacheJdbcBlobStoreFactory()
            .setCreateTableQuery(PostgresBlobCacheStore.DFLT_CREATE_TBL_QRY)
            .setDataSource(dataSource)
            // required because of serialization
            .setDataSourceBean(DATA_SOURCE_BEAN_NAME);
    }

    @Bean
    public Ignite ignite(CacheJdbcBlobStoreFactory cacheJdbcBlobStoreFactory) {
        IgniteConfiguration igniteConfiguration = this.prepareIgniteConfiguration(cacheJdbcBlobStoreFactory);

        try {
            return IgniteSpring.start(igniteConfiguration, applicationContext);
        } catch (IgniteCheckedException icExc) {
            throw new RuntimeException("failed to start Ignite", icExc);
        }
    }

//========================================
// Internals
//----------------------------------------

    private IgniteConfiguration prepareIgniteConfiguration(CacheJdbcBlobStoreFactory cacheJdbcBlobStoreFactory) {
        org.apache.ignite.configuration.IgniteConfiguration igniteConfiguration = new org.apache.ignite.configuration.IgniteConfiguration();
        CacheConfiguration<SessionKey, TwinUpdate> twinCache = new CacheConfiguration<>();
        twinCache.setName(AbstractTwinPublisher.TWIN_TRACKER_CACHE_NAME)
            .setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL)
            .setCacheStoreFactory(cacheJdbcBlobStoreFactory)
            .setWriteThrough(true)
            .setReadThrough(true);
        igniteConfiguration.setCacheConfiguration(
            twinCache
        );
        igniteConfiguration.setClassLoader(applicationContext.getClassLoader());

        // enable compute calls from thin client
        ThinClientConfiguration thinClientConfiguration = new ThinClientConfiguration();
        thinClientConfiguration.setMaxActiveComputeTasksPerConnection(100);

        igniteConfiguration.setClientMode(false);
        igniteConfiguration.setMetricsLogFrequency(0);  // DISABLE IGNITE METRICS
        igniteConfiguration.setClientConnectorConfiguration(new ClientConnectorConfiguration().setThinClientConfiguration(thinClientConfiguration)); // enable client connector

        if (useKubernetes) {
            configureClusterNodeDiscoveryKubernetes(igniteConfiguration);
        } else {
            configureClusterNodeDiscovery(igniteConfiguration);
        }

        configureDataStorage(igniteConfiguration);

        return igniteConfiguration;
    }

    private void configureClusterNodeDiscovery(org.apache.ignite.configuration.IgniteConfiguration igniteConfiguration) {
        TcpDiscoverySpi tcpDiscoverySpi = new TcpDiscoverySpi();

        // Using port 47401 to separate this cluster from the minion one
        TcpDiscoveryMulticastIpFinder ipFinder = new TcpDiscoveryMulticastIpFinder().setMulticastPort(47401);
        tcpDiscoverySpi.setIpFinder(ipFinder);

        igniteConfiguration.setDiscoverySpi(tcpDiscoverySpi);
    }

    private void configureClusterNodeDiscoveryKubernetes(org.apache.ignite.configuration.IgniteConfiguration igniteConfiguration) {
        TcpDiscoverySpi tcpDiscoverySpi = new TcpDiscoverySpi();

        KubernetesConnectionConfiguration connectionConfiguration = new KubernetesConnectionConfiguration();
        connectionConfiguration.setServiceName(kubernetesServiceName);

        TcpDiscoveryKubernetesIpFinder ipFinder = new TcpDiscoveryKubernetesIpFinder(connectionConfiguration);
        tcpDiscoverySpi.setIpFinder(ipFinder);

        igniteConfiguration.setDiscoverySpi(tcpDiscoverySpi);
    }

    private void configureDataStorage(org.apache.ignite.configuration.IgniteConfiguration igniteConfiguration) {
        DataStorageConfiguration dataStorageConfiguration = new DataStorageConfiguration();
        dataStorageConfiguration.getDefaultDataRegionConfiguration().setPersistenceEnabled(false);

        igniteConfiguration.setDataStorageConfiguration(dataStorageConfiguration);
    }
}
