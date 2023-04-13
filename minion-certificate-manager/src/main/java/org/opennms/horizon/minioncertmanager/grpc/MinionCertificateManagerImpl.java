package org.opennms.horizon.minioncertmanager.grpc;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import org.opennms.horizon.minioncertmanager.certificate.PKCS8Generator;
import org.opennms.horizon.minioncertmanager.proto.GetMinionCertificateRequest;
import org.opennms.horizon.minioncertmanager.proto.GetMinionCertificateResponse;
import org.opennms.horizon.minioncertmanager.proto.MinionCertificateManagerGrpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@Component
public class MinionCertificateManagerImpl extends MinionCertificateManagerGrpc.MinionCertificateManagerImplBase {

    private static final Logger LOG = LoggerFactory.getLogger(MinionCertificateManagerImpl.class);

    private final PKCS8Generator pkcs8Generator;

    public MinionCertificateManagerImpl(PKCS8Generator pkcs8Generator) {
        this.pkcs8Generator = pkcs8Generator;
    }

    @Override
    public void getMinionCert(GetMinionCertificateRequest request, StreamObserver<GetMinionCertificateResponse> responseObserver) {
        try {
            File directory = new File(System.getProperty("user.dir"));
            pkcs8Generator.generate(request.getLocation(), request.getTenantId(), directory);
            // Retrieve the generated files
            File pkcs1KeyFile = new File(directory, "client.key.pkcs1");
            LOG.info("PKCS1 key file exists: {}", pkcs1KeyFile.exists());
            LOG.info("PKCS1 location: {}", pkcs1KeyFile.getAbsolutePath());
            File pkcs8KeyFile = new File(directory, "client.key");
            LOG.info("PKCS8 key file exists: {}", pkcs8KeyFile.exists());
            LOG.info("PKCS8 location: {}", pkcs8KeyFile.getAbsolutePath());
            File unsignedCertFile = new File(directory, "client.unsigned.cert");
            LOG.info("Unsigned cert file exists: {}", unsignedCertFile.exists());
            LOG.info("Unsigned cert location: {}", unsignedCertFile.getAbsolutePath());
            File signedCertFile = new File(directory, "client.signed.cert");
            LOG.info("Signed cert file exists: {}", signedCertFile.exists());
            LOG.info("Signed cert location: {}", signedCertFile.getAbsolutePath());
            // Check if the files are generated
            if (pkcs1KeyFile.exists() && pkcs8KeyFile.exists() && unsignedCertFile.exists() && signedCertFile.exists()) {
                String password = UUID.randomUUID().toString();

                // Create a zip file
                try (FileOutputStream fos = new FileOutputStream("minioncert.zip");
                     ZipOutputStream zos = new ZipOutputStream(fos)) {

                    // Add the generated files to the zip
                    addToZip(pkcs1KeyFile, zos);
                    addToZip(pkcs8KeyFile, zos);
                    addToZip(unsignedCertFile, zos);
                    addToZip(signedCertFile, zos);
                    LOG.info("Certificates zip file created.");

                    zos.finish();
                    zos.close();

                    // Read the created zip file into a byte array
                    byte[] zipBytes = Files.readAllBytes(Paths.get("minioncert.zip"));
                    LOG.debug("Zip file: {}", zipBytes);
                    GetMinionCertificateResponse response = GetMinionCertificateResponse.newBuilder()
                        .setCertificate(ByteString.copyFrom(zipBytes))
                        .setPassword(password).build();
                    responseObserver.onNext(response);
                    responseObserver.onCompleted();
                }
            } else {
                LOG.error("Failed to generate one or more files.");
                responseObserver.onError(new RuntimeException("Failed to generate one or more files."));
            }
        } catch (IOException | InterruptedException e) {
            LOG.error("Error while fetching certificate", e);
            responseObserver.onError(e);
        }
    }

    private void addToZip(File file, ZipOutputStream zos) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            ZipEntry zipEntry = new ZipEntry(file.getName());
            zos.putNextEntry(zipEntry);

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                zos.write(buffer, 0, bytesRead);
            }

            zos.closeEntry();
        }
    }
}
