/*
 * acme4j - Java ACME client
 *
 * Copyright (C) 2015 Richard "Shred" Körber
 *   http://acme4j.shredzone.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package org.opennms.horizon.minioncertmanager.certificate;

import org.awaitility.Awaitility;
import org.awaitility.core.ConditionTimeoutException;
import org.shredzone.acme4j.Account;
import org.shredzone.acme4j.AccountBuilder;
import org.shredzone.acme4j.Authorization;
import org.shredzone.acme4j.Certificate;
import org.shredzone.acme4j.Order;
import org.shredzone.acme4j.Session;
import org.shredzone.acme4j.Status;
import org.shredzone.acme4j.challenge.Challenge;
import org.shredzone.acme4j.challenge.Dns01Challenge;
import org.shredzone.acme4j.challenge.Http01Challenge;
import org.shredzone.acme4j.exception.AcmeException;
import org.shredzone.acme4j.util.CSRBuilder;
import org.shredzone.acme4j.util.KeyPairUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.security.KeyPair;
import java.time.Instant;
import java.util.Collection;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * A simple client test tool.
 * <p>
 * Pass the names of the domains as parameters.
 */
@Component
public class CertClient {
    // File name of the User Key Pair
    private static final File USER_KEY_FILE = new File("user.key");

    // File name of the Domain Key Pair
    private static final File DOMAIN_KEY_FILE = new File("domain.key");

    //Challenge type to be used
    private static final ChallengeType CHALLENGE_TYPE = ChallengeType.HTTP;

    // RSA key size of generated key pairs
    private static final int KEY_SIZE = 2048;

    private static final Logger LOG = LoggerFactory.getLogger(CertClient.class);

    private enum ChallengeType {HTTP, DNS}

    @Value("${certificate-authority.url}")
    private String certificateAuthorityUrl;

    /**
     * Generates a certificate for the given domains. Also takes care for the registration
     * process.
     *
     * @param domains    Domains to get a common certificate for
     * @param userKeyPair User Key Pair
     * @param locationId Location ID
     * @param tenantId   Tenant ID
     * @return {@link Certificate} for the domains
     */
    public Certificate fetchCertificate(Collection<String> domains, KeyPair userKeyPair, Long locationId, String tenantId) throws IOException, AcmeException {
        // Create a session for Let's Encrypt.
        Session session = new Session(certificateAuthorityUrl);

        // Get the Account.
        // If there is no account yet, create a new one.
        Account acct = findOrRegisterAccount(session, userKeyPair);

        // Load or create a key pair for the domains. This should not be the userKeyPair!
        KeyPair domainKeyPair = loadOrCreateDomainKeyPair();

        // Order the certificate
        Order order = acct.newOrder().domains(domains).create();

        // Perform all required authorizations
        for (Authorization auth : order.getAuthorizations()) {
            authorize(auth);
        }

        // Generate a Certificate Signing Request for all the domains, and sign it with the domain key pair.
        CSRBuilder csrb = new CSRBuilder();
        csrb.addDomains(domains);
        csrb.setOrganizationalUnit("L:" + locationId);
        csrb.setOrganizationalUnit("T:" + tenantId);
        csrb.sign(domainKeyPair);

        // Order the certificate
        order.execute(csrb.getEncoded());

        // Wait for the order to complete
        try {
            Awaitility.await()
                .atMost(30, SECONDS)
                .pollInterval(3, SECONDS)
                .until(() -> order.getStatus() == Status.VALID);
        } catch (ConditionTimeoutException ex) {
            LOG.error("Order did not complete within timeout", ex);
            throw new AcmeException("Order failed... Giving up.");
        }

        // Get the certificate
        Certificate certificate = order.getCertificate();

        LOG.info("Success! The certificate for domains {} has been generated!", domains);
        LOG.info("Certificate URL: {}", certificate != null ? certificate.getLocation() : null);

        return certificate;
    }

    /**
     * Loads a user key pair from {@link #USER_KEY_FILE}. If the file does not exist, a
     * new key pair is generated and saved.
     * <p>
     * Keep this key pair in a safe place! In a production environment, you will not be
     * able to access your account again if you should lose the key pair.
     *
     * @return User's {@link KeyPair}.
     */
    public KeyPair loadOrCreateUserKeyPair() throws IOException {
        if (USER_KEY_FILE.exists()) {
            // If there is a key file, read it
            try (FileReader fr = new FileReader(USER_KEY_FILE)) {
                return KeyPairUtils.readKeyPair(fr);
            }
        } else {
            // If there is none, create a new key pair and save it
            KeyPair userKeyPair = KeyPairUtils.createKeyPair(KEY_SIZE);
            try (FileWriter fw = new FileWriter(USER_KEY_FILE)) {
                KeyPairUtils.writeKeyPair(userKeyPair, fw);
            }
            return userKeyPair;
        }
    }

    /**
     * Loads a domain key pair from {@link #DOMAIN_KEY_FILE}. If the file does not exist,
     * a new key pair is generated and saved.
     *
     * @return Domain {@link KeyPair}.
     */
    private KeyPair loadOrCreateDomainKeyPair() throws IOException {
        if (DOMAIN_KEY_FILE.exists()) {
            try (FileReader fr = new FileReader(DOMAIN_KEY_FILE)) {
                return KeyPairUtils.readKeyPair(fr);
            }
        } else {
            KeyPair domainKeyPair = KeyPairUtils.createKeyPair(KEY_SIZE);
            try (FileWriter fw = new FileWriter(DOMAIN_KEY_FILE)) {
                KeyPairUtils.writeKeyPair(domainKeyPair, fw);
            }
            return domainKeyPair;
        }
    }

    /**
     * Finds your {@link Account} at the ACME server. It will be found by your user's
     * public key. If your key is not known to the server yet, a new account will be
     * created.
     * <p>
     * This is a simple way of finding your {@link Account}. A better way is to get the
     * URL of your new account with {@link Account#getLocation()} and store it somewhere.
     * If you need to get access to your account later, reconnect to it via {@link
     * Session#login(URL, KeyPair)} by using the stored location.
     *
     * @param session {@link Session} to bind with
     * @return {@link Account}
     */
    private Account findOrRegisterAccount(Session session, KeyPair accountKey) throws AcmeException {
        Account account = new AccountBuilder()
            .agreeToTermsOfService()
            .useKeyPair(accountKey)
            .create(session);
        LOG.info("Registered user, URL: {}", account.getLocation());

        return account;
    }

    /**
     * Authorize a domain. It will be associated with your account, so you will be able to
     * retrieve a signed certificate for the domain later.
     *
     * @param auth {@link Authorization} to perform
     */
    private void authorize(Authorization auth) throws AcmeException {
        LOG.info("Authorization for domain {}", auth.getIdentifier().getDomain());

        // The authorization is already valid. No need to process a challenge.
        if (auth.getStatus() == Status.VALID && auth.getExpires() != null && auth.getExpires().isAfter(Instant.now())) {
            return;
        }

        // Find the desired challenge and prepare it.
        Challenge challenge;
        switch (CHALLENGE_TYPE) {
            case HTTP -> challenge = httpChallenge(auth);
            case DNS -> challenge = dnsChallenge(auth);
            default -> throw new AcmeException("Unknown challenge type: " + CHALLENGE_TYPE);
        }

        // If the challenge is already verified, there's no need to execute it again.
        if (challenge.getStatus() == Status.VALID) {
            return;
        }

        // Now trigger the challenge.
        challenge.trigger();

        // Poll for the challenge to complete.
        try {
            Awaitility.await()
                .atMost(30, SECONDS)
                .pollInterval(3, SECONDS)
                .until(() -> {
                    // Update the challenge status
                    challenge.update();
                    return challenge.getStatus() == Status.VALID;
                });
        } catch (ConditionTimeoutException ex) {
            LOG.error("Challenge did not complete within timeout", ex);
            throw new AcmeException("Challenge failed... Giving up.");
        }

        // All reattempts are used up and there is still no valid authorization?
        if (challenge.getStatus() != Status.VALID) {
            throw new AcmeException("Failed to pass the challenge for domain "
                + auth.getIdentifier().getDomain() + ", ... Giving up.");
        }

        LOG.info("Challenge has been completed. Remember to remove the validation resource.");
//        completeChallenge("Challenge has been completed.\nYou can remove the resource again now.");
    }

    /**
     * Prepares an HTTP challenge.
     * <p>
     * The verification of this challenge expects a file with a certain content to be
     * reachable at a given path under the domain to be tested.
     * <p>
     * This example outputs instructions that need to be executed manually. In a
     * production environment, you would rather generate this file automatically, or maybe
     * use a servlet that returns {@link Http01Challenge#getAuthorization()}.
     *
     * @param auth {@link Authorization} to find the challenge in
     * @return {@link Challenge} to verify
     */
    private Challenge httpChallenge(Authorization auth) throws AcmeException {
        // Find a single http-01 challenge
        Http01Challenge challenge = auth.findChallenge(Http01Challenge.class);
        if (challenge == null) {
            throw new AcmeException("Found no " + Http01Challenge.TYPE + " challenge, don't know what to do...");
        }

        return challenge;
    }

    /**
     * Prepares a DNS challenge.
     * <p>
     * The verification of this challenge expects a TXT record with a certain content.
     * <p>
     * This example outputs instructions that need to be executed manually. In a
     * production environment, you would rather configure your DNS automatically.
     *
     * @param auth {@link Authorization} to find the challenge in
     * @return {@link Challenge} to verify
     */
    private Challenge dnsChallenge(Authorization auth) throws AcmeException {
        // Find a single dns-01 challenge
        Dns01Challenge challenge = auth.findChallenge(Dns01Challenge.TYPE);
        if (challenge == null) {
            throw new AcmeException("Found no " + Dns01Challenge.TYPE + " challenge, don't know what to do...");
        }

        return challenge;
    }
}
