/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2022 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2022 The OpenNMS Group, Inc.
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

package org.opennms.horizon.shared.azure.http;

import lombok.Getter;
import org.opennms.horizon.shared.azure.http.dto.error.AzureErrorDescription;

@Getter
public class AzureHttpException extends Exception {
    private final transient AzureErrorDescription description;

    public AzureHttpException(String message, AzureErrorDescription description) {
        super(message);
        this.description = description;
    }

    public AzureHttpException(String message) {
        super(message);
        this.description = null;
    }

    public AzureHttpException(String message, Throwable t) {
        super(message, t);
        this.description = null;
    }

    public boolean hasDescription() {
        return this.description != null;
    }
}
