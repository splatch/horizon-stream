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

import org.shredzone.acme4j.Certificate;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.cert.CertificateEncodingException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
public class CertZipper {

    public ByteArrayOutputStream createEncryptedZip(java.security.PrivateKey privateKey, Certificate certificate)
        throws IOException, CertificateEncodingException {

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        // Create a ZipOutputStream to write the encrypted zip file
        try (ZipOutputStream zos = new ZipOutputStream(os)) {

            // Add the certificate to the zip file
            ZipEntry certEntry = new ZipEntry("certificate.cer");
            zos.putNextEntry(certEntry);
            zos.write(certificate.getCertificate().getEncoded());
            zos.closeEntry();

            // Add the private key to the zip file
            ZipEntry privateKeyEntry = new ZipEntry("key.pem");
            zos.putNextEntry(privateKeyEntry);
            zos.write(privateKey.getEncoded());
            zos.closeEntry();

            // Close the zip output stream
            zos.finish();
        }
        return os;
    }
}
