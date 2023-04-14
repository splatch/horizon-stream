/*******************************************************************************
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
 *******************************************************************************/
package org.opennms.horizon.minioncertmanager.grpc;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.opennms.horizon.minioncertmanager.certificate.CertFileUtils;
import org.opennms.horizon.minioncertmanager.certificate.PKCS8Generator;
import org.opennms.horizon.minioncertmanager.proto.GetMinionCertificateRequest;
import org.opennms.horizon.minioncertmanager.proto.GetMinionCertificateResponse;
import org.opennms.horizon.minioncertmanager.proto.MinionCertificateManagerGrpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;


@Component
@RequiredArgsConstructor
public class MinionCertificateManagerImpl extends MinionCertificateManagerGrpc.MinionCertificateManagerImplBase {

    private static final Logger LOG = LoggerFactory.getLogger(MinionCertificateManagerImpl.class);
    private static final String FAILED_TO_GENERATE_ONE_OR_MORE_FILES = "Failed to generate one or more files.";
    public static final String HORIZON_STREAM_CERTIFICATES = "horizon-stream-certificates";

    private final PKCS8Generator pkcs8Generator;
    private final CertFileUtils certFileUtils;
    private final File directory = new File(System.getProperty("user.dir"), HORIZON_STREAM_CERTIFICATES);

    @Override
    public void getMinionCert(GetMinionCertificateRequest request, StreamObserver<GetMinionCertificateResponse> responseObserver) {
        try {
            String password = UUID.randomUUID().toString();
            if (!directory.exists()){
                directory.mkdirs();
            }
            File file = new File(directory, "minioncert.zip");
            if (!file.exists()) {
                pkcs8Generator.generate(request.getLocation(), request.getTenantId(), directory);
                if (!validatePKCS8Files(directory)) {
                    LOG.error(FAILED_TO_GENERATE_ONE_OR_MORE_FILES);
                    responseObserver.onError(new RuntimeException(FAILED_TO_GENERATE_ONE_OR_MORE_FILES));
                    return;
                }
                certFileUtils.createZipFile(file, password, directory);
            }

            byte[] zipBytes = certFileUtils.readZipFileToByteArray(file);
            responseObserver.onNext(createResponse(zipBytes, password));
            responseObserver.onCompleted();
        } catch (IOException | InterruptedException e) {
            LOG.error("Error while fetching certificate", e);
            responseObserver.onError(e);
        } finally {
            cleanFiles(directory);
        }
    }

    private boolean validatePKCS8Files(File directory) {
        File caCertFile = new File(directory, "CA.cert");
        File clientKeyFile = new File(directory, "client.key");
        File clientSignedCertFile = new File(directory, "client.signed.cert");
        return caCertFile.exists() && clientKeyFile.exists() && clientSignedCertFile.exists();
    }

    private GetMinionCertificateResponse createResponse(byte[] zipBytes, String password) {
        return GetMinionCertificateResponse.newBuilder()
            .setCertificate(ByteString.copyFrom(zipBytes))
            .setPassword(password)
            .build();
    }

    private void cleanFiles(File directory){
        File[] files = directory.listFiles();
        if (files != null) {
            Arrays.stream(files).filter(file -> !file.getName().endsWith(".zip")).forEach(File::delete);
        }
    }
}
