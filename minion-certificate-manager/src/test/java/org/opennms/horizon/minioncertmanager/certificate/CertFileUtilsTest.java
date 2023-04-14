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

import net.lingala.zip4j.ZipFile;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class CertFileUtilsTest {

    @InjectMocks
    private CertFileUtils certFileUtils;
    private static File tempDir;

    @BeforeAll
    public static void setUp() {
        String classpathDir = CertFileUtilsTest.class.getClassLoader().getResource("").getFile();
        tempDir = new File(classpathDir, "tempDir");
        tempDir.mkdir();
    }

    @AfterAll
    public static void tearDown() {
        //clean files in tempDir
        Arrays.stream(tempDir.listFiles()).forEach(File::delete);
        tempDir.delete();
        assertFalse(tempDir.exists());
    }

    @Test
    public void testCreateZipFile() throws Exception {
        File tempFile1 = new File(tempDir, "client.key");
        tempFile1.createNewFile();
        File tempFile2 = new File(tempDir, "CA.cert");
        tempFile2.createNewFile();
        File tempFile3 = new File(tempDir, "client.signed.cert");
        tempFile3.createNewFile();

        File file = new File(tempDir, "minioncert.zip");
        String password = "password";

        certFileUtils.createZipFile(file, password, tempDir);

        // Verify the contents of the created zip file
        try (ZipFile zip = new ZipFile(file)) {
            assertTrue(zip.isValidZipFile());
            assertEquals(3, zip.getFileHeaders().size());
            assertTrue(zip.isEncrypted());
        }
    }
}

