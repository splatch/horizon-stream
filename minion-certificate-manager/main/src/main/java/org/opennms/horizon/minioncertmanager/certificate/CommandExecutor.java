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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

public class CommandExecutor {
    private static final Logger LOG = LoggerFactory.getLogger(CommandExecutor.class);
    private static final int TIMEOUT = 5000;

    public static void executeCommand(String command) throws IOException, InterruptedException {
        executeCommand(command, null);
    }

    public static void executeCommand(String command, File directory) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", command).directory(directory);
        Process process = null;
        BufferedReader stdoutReader = null;
        BufferedReader stderrReader = null;
        try {
            process = processBuilder.start();

            // Read the output from the process's stdout and stderr
            stdoutReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            stderrReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            String stdoutLine;
            while ((stdoutLine = stdoutReader.readLine()) != null) {
                LOG.debug(stdoutLine);
            }

            String stderrLine;
            while ((stderrLine = stderrReader.readLine()) != null) {
                LOG.error(stderrLine); // Log stderr output as error
            }

            // Wait for the process to complete with a configurable timeout
            boolean completed = process.waitFor(TIMEOUT, TimeUnit.MILLISECONDS);
            if (!completed) {
                LOG.error("Command timed out: " + command + ". Timeout: " + TIMEOUT + " seconds");
            }

            // Check the exit value of the process
            int exitValue = process.exitValue();
            if (exitValue != 0) {
                LOG.error("Command exited with error code: " + exitValue + ". Command: " + command);
            }
        } finally {
            if (process != null) {
                process.destroy();
            }
            if (stdoutReader != null) {
                stdoutReader.close();
            }
            if (stderrReader != null) {
                stderrReader.close();
            }
        }
    }
}
