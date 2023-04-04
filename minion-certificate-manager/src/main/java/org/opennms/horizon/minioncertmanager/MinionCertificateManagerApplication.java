package org.opennms.horizon.minioncertmanager;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.security.Security;

@SpringBootApplication
public class MinionCertificateManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MinionCertificateManagerApplication.class, args);

        Security.addProvider(new BouncyCastleProvider());
	}

}
