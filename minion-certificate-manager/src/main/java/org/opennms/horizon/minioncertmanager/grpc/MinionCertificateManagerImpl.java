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
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.cert.CertificateEncodingException;
import java.util.Collections;


@Component
public class MinionCertificateManagerImpl extends MinionCertificateManagerGrpc.MinionCertificateManagerImplBase {

    private final CertClient certClient;

    private final CertZipper certZipper;

    public MinionCertificateManagerImpl(CertClient certClient, CertZipper certZipper) {
        this.certClient = certClient;
        this.certZipper = certZipper;
    }

    @Override
    public void getMinionCert(GetMinionCertificateRequest request, StreamObserver<GetMinionCertificateResponse> responseObserver) {
        try {
            Certificate cert = certClient.fetchCertificate(Collections.singleton("test"));
            try (ByteArrayOutputStream zip = certZipper.getZip(cert)) {
                GetMinionCertificateResponse response = GetMinionCertificateResponse.newBuilder()
                    .setZip(ByteString.copyFrom(zip.toByteArray()))
                    .build();
                responseObserver.onNext(response);
            }
            responseObserver.onCompleted();
        } catch (IOException | AcmeException | CertificateEncodingException e) {
            responseObserver.onError(e);
        }
    }
}
