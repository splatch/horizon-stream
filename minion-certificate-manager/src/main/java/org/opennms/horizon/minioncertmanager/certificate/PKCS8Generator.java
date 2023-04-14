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

package org.opennms.horizon.minioncertmanager.certificate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

@Component
public class PKCS8Generator {
    private static final Logger LOG = LoggerFactory.getLogger(PKCS8Generator.class);

    public void generate(String location, String tenantId, File directory) throws InterruptedException, IOException {
        LOG.info("=== GENERATING CERTIFICATE FOR LOCATION: {} AND TENANT: {}", location, tenantId);
        LOG.debug("=== GENERATE CA CERT");
        executeCommand("openssl req -new -newkey rsa:4096 -days 3650 -nodes -x509 -subj \"/C=CA/ST=TBD/L=TBD/O=OpenNMS/CN=insecure-opennms-hs-ca\" -keyout CA.key -out CA.cert", directory);

        LOG.debug("=== MAKING PKCS1 KEY");
        executeCommand("openssl genrsa -out client.key.pkcs1 2048", directory);

        LOG.debug("=== CONVERTING TO PKCS8");
        executeCommand("openssl pkcs8 -topk8 -in client.key.pkcs1 -out client.key -nocrypt", directory);

        LOG.debug("=== GENERATING THE UNSIGNED CERT");
        executeCommand("openssl req -new -key client.key -out client.unsigned.cert -subj \"/C=CA/ST=TBD/L=TBD/O=OpenNMS/CN=opennms-minion-ssl-gateway/OU=L:" + location + "/OU=T:" + tenantId + "\"", directory);

        LOG.debug("=== SIGNING CERT");
        executeCommand("openssl x509 -req -in client.unsigned.cert -days 3650 -CA CA.cert -CAkey CA.key -CAcreateserial -out client.signed.cert", directory);

        LOG.info("=== DONE");
    }

    private static void executeCommand(String command, File directory) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", command).directory(directory);
        Process process = processBuilder.start();

        // Read the output from the process's stdout and stderr
        BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader stderrReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        String stdoutLine;
        while ((stdoutLine = stdoutReader.readLine()) != null) {
            LOG.debug(stdoutLine);
        }

        String stderrLine;
        while ((stderrLine = stderrReader.readLine()) != null) {
            LOG.error(stderrLine); // Log stderr output as error
        }

        // Wait for the process to complete
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            LOG.error("Command exited with error code: " + exitCode);
        }
    }
}
