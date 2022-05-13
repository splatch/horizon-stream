/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2009-2022 The OpenNMS Group, Inc.
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

package org.opennms.netmgt.provision.persistence.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Data;

@Data
public class RequisitionNodeDTO {

    private final String foreignId;
    private final String m_location;
    private final String building;
    private final String city;
    private final String nodeLabel;
    private String parentForeignId;
    private String parentForeignSource;
    
    //TODO: anti-pattern! Remove this
    protected String parentNodeLabel;

    protected Map<String, RequisitionInterfaceDTO> interfaces = new HashMap<>();
    protected Map<String, RequisitionCategoryDTO> categories = new HashMap<>();
    protected Map<String, RequisitionMetaDataDTO> metaData = new HashMap<>();

    /**
     * <p>getInterfaceCount</p>
     *
     * @return a int.
     */
    public int getInterfaceCount() {
        return interfaces.size();
    }

    /**
     * <p>getInterfaces</p>
     *
     * @return a {@link List} object.
     */
    public List<RequisitionInterfaceDTO> getInterfacesAsList() {
        return interfaces.entrySet().stream().map(e -> e.getValue()).collect(Collectors.toList());
    }

    public List<RequisitionMetaDataDTO> getMetadataAsList() {
        return metaData.entrySet().stream().map(e -> e.getValue()).collect(Collectors.toList());
    }

    public List<RequisitionCategoryDTO> getCategoriesAsList() {
        return categories.entrySet().stream().map(e -> e.getValue()).collect(Collectors.toList());
    }

    //TODO: do we want to allow this?
//    /**
//     * <p>setInterfaces</p>
//     *
//     * @param interfaces a {@link List} object.
//     */
//    public void setInterfaces(Collection<RequisitionInterface> interfaces) {
//        if (interfaces == null) {
//            interfaces = new TreeSet<>();
//        }
//        if (this.interfaces == interfaces) return;
//        this.interfaces.clear();
//        this.interfaces.addAll(interfaces);
//    }

    /**
     * <p>getInterface</p>
     *
     * @param ipAddress a {@link String} object.
     * @return a {@link RequisitionInterfaceDTO} object.
     */
    public RequisitionInterfaceDTO getInterface(String ipAddress) {
        return interfaces.get(ipAddress);
    }

    /**
     * <p>removeInterface</p>
     *
     * @param iface a {@link RequisitionInterfaceDTO} object.
     */
    public boolean deleteInterface(final RequisitionInterfaceDTO iface) {
        return deleteInterface(iface.ipAddressStr);
    }

    /**
     * <p>deleteInterface</p>
     *
     * @param ipAddress a {@link String} object.
     */
    public boolean deleteInterface(final String ipAddress) {
        return (interfaces.remove(ipAddress) != null);
    }

    /**
     * <p>putInterface</p>
     *
     * @param iface a {@link RequisitionInterfaceDTO} object.
     */
    public void putInterface(RequisitionInterfaceDTO iface) {
        interfaces.put(iface.ipAddressStr, iface);
    }

    /**
     * <p>getCategoryCount</p>
     *
     * @return a int.
     */
    public int getCategoryCount() {
        return (categories == null)? 0 : categories.size();
    }

//    /**
//     * <p>setCategories</p>
//     *
//     * @param categories a {@link List} object.
//     */
//    public void setCategories(Collection<RequisitionCategory> categories) {
//        if (categories == null) {
//            categories = new TreeSet<>();
//        }
//        if (this.categories == categories) return;
//        this.categories.clear();
//        this.categories.addAll(categories);
//    }

    /**
     * <p>getCategory</p>
     *
     * @param category a {@link String} object.
     * @return a {@link RequisitionCategoryDTO} object.
     */
    public RequisitionCategoryDTO getCategory(String category) {
        return categories.get(category);
    }

    /**
     * <p>deleteCategory</p>
     *
     * @param category a {@link RequisitionCategoryDTO} object.
     */
    public boolean deleteCategory(final RequisitionCategoryDTO category) {
        return deleteCategory(category.getName());
    }

    /**
     * <p>deleteCategory</p>
     *
     * @param category a {@link String} object.
     */
    public boolean deleteCategory(final String category) {
        return (categories.remove(category) != null);
    }

    /**
     * <p>putCategory</p>
     *
     * @param category a {@link RequisitionCategoryDTO} object.
     */
    public void putCategory(RequisitionCategoryDTO category) {
        categories.put(category.getName(), category);
    }

    /**
     * <p>Setter for the field <code>parentForeignSource</code>.</p>
     *
     * @param value a {@link String} object.
     */
    public void setParentForeignSource(String value) {
        parentForeignSource = value != null && "".equals(value.trim()) ? null : value;
    }

    /**
     * <p>Setter for the field <code>parentForeignId</code>.</p>
     *
     * @param value a {@link String} object.
     */
    public void setParentForeignId(String value) {
        parentForeignId = value != null && "".equals(value.trim()) ? null : value;
    }

    /**
     * <p>Setter for the field <code>parentNodeLabel</code>.</p>
     *
     * @param value a {@link String} object.
     */
    public void setParentNodeLabel(String value) {
        parentNodeLabel = value != null && "".equals(value.trim()) ? null : value;
    }

//    public void validate() throws ValidationException {
//        //this.pruneInterfaces();
//        if (m_nodeLabel == null) {
//            throw new ValidationException("Requisition node 'node-label' is a required attribute!");
//        }
//        if (m_foreignId == null) {
//            throw new ValidationException("Requisition node 'foreign-id' is a required attribute!");
//        }
//        if (m_foreignId.contains("/")) {
//            throw new ValidationException("Node foreign ID (" + m_foreignId + ") contains invalid characters. ('/' is forbidden.)");
//        }
//        if (m_interfaces != null) {
//            Iterator<RequisitionInterface> iter = m_interfaces.iterator();
//            while (iter.hasNext()) {
//                try {
//                    iter.next().validate(this);
//                }
//                catch (IPValidationException ive) {
//                    iter.remove();
//                }
//            }
//            // there can be only one primary interface per node
//            if(m_interfaces.stream().filter(iface -> PrimaryType.PRIMARY == iface.snmpPrimary).count() > 1) {
//                throw new ValidationException("Node foreign ID (" + m_foreignId + ") contains multiple primary interfaces. Maximum one is allowed.");
//            }
//        }
//        if (m_categories != null) {
//            for (final RequisitionCategory cat : m_categories) {
//                cat.validate();
//            }
//        }
//        if (m_assets != null) {
//            for (final RequisitionAsset asset : m_assets) {
//                asset.validate();
//            }
//        }
//    }

//    @Override
//    public int hashCode() {
//        return Objects.hash(m_building, m_city, m_foreignId, m_assets,
//                m_categories, m_interfaces, m_nodeLabel, m_nodeLabel,
//                m_parentForeignId, m_parentForeignSource, m_parentNodeLabel, m_location,
//                m_metaData);
//    }
//
//    @Override
//    public boolean equals(final Object obj) {
//        if (this == obj) return true;
//        if (obj == null) return false;
//        if (!(obj instanceof RequisitionNode)) return false;
//        final RequisitionNode other = (RequisitionNode) obj;
//        return Objects.equals(this.m_building, other.m_building) &&
//                Objects.equals(this.m_city, other.m_city) &&
//                Objects.equals(this.m_foreignId, other.m_foreignId) &&
//                Objects.equals(this.m_assets, other.m_assets) &&
//                Objects.equals(this.m_categories, other.m_categories) &&
//                Objects.equals(this.m_interfaces, other.m_interfaces) &&
//                Objects.equals(this.m_nodeLabel, other.m_nodeLabel) &&
//                Objects.equals(this.m_parentForeignId, other.m_parentForeignId) &&
//                Objects.equals(this.m_parentForeignSource, other.m_parentForeignSource) &&
//                Objects.equals(this.m_parentNodeLabel, other.m_parentNodeLabel) &&
//                Objects.equals(this.m_location, other.m_location) &&
//                Objects.equals(this.m_metaData, other.m_metaData);
//    }
//
//    @Override
//    public String toString() {
//        return "RequisitionNode [interfaces=" + m_interfaces
//                + ", categories=" + m_categories + ", assets=" + m_assets + ", meta-data=" + m_metaData
//                + ", building=" + m_building + ", city=" + m_city
//                + ", foreignId=" + m_foreignId + ", nodeLabel=" + m_nodeLabel
//                + ", parentForeignSource=" + m_parentForeignSource
//                + ", parentForeignId=" + m_parentForeignId
//                + ", parentNodeLabel=" + m_parentNodeLabel
//                + ", location=" + m_location + "]";
//    }
}
