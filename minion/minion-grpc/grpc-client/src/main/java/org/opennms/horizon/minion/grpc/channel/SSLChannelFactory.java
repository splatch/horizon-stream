/*
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2023 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2023 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *
 */

package org.opennms.horizon.minion.grpc.channel;

import io.grpc.ManagedChannel;
import io.grpc.TlsChannelCredentials;
import io.grpc.TlsChannelCredentials.Builder;
import java.io.File;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import lombok.Setter;
import org.opennms.horizon.minion.grpc.ssl.KeyStoreFactory;

public class SSLChannelFactory implements ManagedChannelFactory {

    private final ChannelBuilderFactory channelBuilderFactory;

    private final KeyStoreFactory keyStoreFactory;

    @Setter
    private String keyStore;
    @Setter
    private String keyStoreType;
    @Setter
    private String keyStorePassword;
    @Setter
    private String trustStore;
    @Setter
    private String trustStoreType;
    @Setter
    private String trustStorePassword;

    public SSLChannelFactory(ChannelBuilderFactory channelBuilderFactory, KeyStoreFactory keyStoreFactory) {
        this.channelBuilderFactory = channelBuilderFactory;
        this.keyStoreFactory = keyStoreFactory;
    }

    @Override
    public ManagedChannel create(String hostname, int port, String authority) {
        Builder credentials = TlsChannelCredentials.newBuilder();

        if (keyStore != null && !keyStore.isBlank()) {
            try {
                KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                keyManagerFactory.init(loadKeyStore(keyStoreType, keyStore, keyStorePassword), keyStorePassword.toCharArray());
                credentials.keyManager(keyManagerFactory.getKeyManagers());
            } catch (GeneralSecurityException e) {
                throw new RuntimeException(e);
            }
        }

        if (trustStore != null && !trustStore.isBlank()) {
            try {
                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init(loadKeyStore(trustStoreType, trustStore, trustStorePassword));
                credentials.trustManager(trustManagerFactory.getTrustManagers());
            } catch (GeneralSecurityException e) {
                throw new RuntimeException(e);
            }
        }

        return channelBuilderFactory.create(hostname, port, authority, credentials.build())
            //.useTransportSecurity()
            .build();
    }

    private KeyStore loadKeyStore(String type, String location, String password) {
        File keyStoreFile = new File(location);
        if (!keyStoreFile.exists() || !keyStoreFile.isFile() || !keyStoreFile.canRead()) {
            throw new IllegalArgumentException("File " + location + " does not exist, is not a file or can not be read");
        }

        try {
            return keyStoreFactory.createKeyStore(type, keyStoreFile, password);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }
}
