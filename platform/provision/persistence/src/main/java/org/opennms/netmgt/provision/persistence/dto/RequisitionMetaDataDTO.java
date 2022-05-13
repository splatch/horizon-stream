/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2019 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2019 The OpenNMS Group, Inc.
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

package org.opennms.netmgt.provision.persistence.dto;

import lombok.Data;

@Data
public class RequisitionMetaDataDTO {

    protected String context;
    protected String key;
    protected String value;

//    @Override
//    public int compareTo(final RequisitionMetaData other) {
//        return new CompareToBuilder()
//            .append(context, other.context)
//            .append(key, other.key)
//            .append(value, other.value)
//            .toComparison();
//    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        RequisitionMetaData that = (RequisitionMetaData) o;
//        return Objects.equals(context, that.context) &&
//                Objects.equals(key, that.key) &&
//                Objects.equals(value, that.value);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(context, key, value);
//    }
}
