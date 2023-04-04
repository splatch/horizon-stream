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

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.shredzone.acme4j.*;
import org.shredzone.acme4j.challenge.Challenge;
import org.shredzone.acme4j.challenge.Dns01Challenge;
import org.shredzone.acme4j.challenge.Http01Challenge;
import org.shredzone.acme4j.exception.AcmeException;
import org.shredzone.acme4j.util.CSRBuilder;
import org.shredzone.acme4j.util.KeyPairUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.security.KeyPair;
import java.security.Security;
import java.security.cert.CertificateEncodingException;
import java.util.Arrays;
import java.util.Collection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * A simple client test tool.
 * <p>
 * Pass the names of the domains as parameters.
 */
@Component
public class CertZipper {
    public ByteArrayOutputStream getZip(Certificate certificate) throws IOException, CertificateEncodingException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        try (ZipOutputStream out = new ZipOutputStream(os)) {
            ZipEntry e = new ZipEntry("domain-chain.crt");
            out.putNextEntry(e);

            byte[] data = certificate.getCertificate().getEncoded();
            out.write(data, 0, data.length);
            out.closeEntry();
        }

        return os;
    }
}
