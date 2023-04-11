package org.opennms.horizon.minioncertmanager.grpc;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import org.opennms.horizon.minioncertmanager.certificate.CertClient;
import org.opennms.horizon.minioncertmanager.certificate.CertZipper;
import org.opennms.horizon.minioncertmanager.proto.GetMinionCertificateRequest;
import org.opennms.horizon.minioncertmanager.proto.GetMinionCertificateResponse;
import org.opennms.horizon.minioncertmanager.proto.MinionCertificateManagerGrpc;
import org.shredzone.acme4j.Certificate;
import org.shredzone.acme4j.exception.AcmeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.cert.CertificateEncodingException;
import java.util.Collections;
import java.util.UUID;


@Component
public class MinionCertificateManagerImpl extends MinionCertificateManagerGrpc.MinionCertificateManagerImplBase {

    private static final Logger LOG = LoggerFactory.getLogger(MinionCertificateManagerImpl.class);

    private final CertClient certClient;
    private final CertZipper certZipper;

    public MinionCertificateManagerImpl(CertClient certClient, CertZipper certZipper) {
        this.certClient = certClient;
        this.certZipper = certZipper;
    }

    @Override
    public void getMinionCert(GetMinionCertificateRequest request, StreamObserver<GetMinionCertificateResponse> responseObserver) {
        try {
            // Load the user key file. If there is no key file, create a new one.
            KeyPair userKeyPair = certClient.loadOrCreateUserKeyPair();
            Certificate cert = certClient.fetchCertificate(Collections.singleton("test"), userKeyPair, request.getLocationId(), request.getTenantId());
            try (ByteArrayOutputStream zip = certZipper.createEncryptedZip(userKeyPair.getPrivate(), cert)) {
                GetMinionCertificateResponse response = GetMinionCertificateResponse.newBuilder()
                    .setZip(ByteString.copyFrom(zip.toByteArray()))
                    .setPassword(UUID.randomUUID().toString()).build();
                responseObserver.onNext(response);
            }
            responseObserver.onCompleted();
        } catch (IOException | AcmeException | CertificateEncodingException e) {
            LOG.error("Error while fetching certificate", e);
            responseObserver.onError(e);
        }
    }
}
