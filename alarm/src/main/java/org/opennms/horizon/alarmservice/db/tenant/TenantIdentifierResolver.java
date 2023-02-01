package org.opennms.horizon.alarmservice.db.tenant;
import io.grpc.Context;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

//@Component
class TenantIdentifierResolver implements CurrentTenantIdentifierResolver, HibernatePropertiesCustomizer {
    private static final Logger LOG = LoggerFactory.getLogger(TenantIdentifierResolver.class);
    private static final String DEFAULT_TENANT_ID = "opennms-prime";

    //@Autowired
    TenantLookup tenantLookup;

    @Override
    public String resolveCurrentTenantIdentifier() {
        Optional<String> tenantId = tenantLookup.lookupTenantId(Context.current());
        if (tenantId.isPresent()) {
            return tenantId.get();
        } else {
            // Much as I think we should possibly throw an exception here, the internet says we should provide a default value.
            // Attempting to throw an exception results in a failure during spring initialization.
            LOG.warn("No tenant ID present");
            return DEFAULT_TENANT_ID;
        }
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return false;
    }

    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER, this);
    }
}
