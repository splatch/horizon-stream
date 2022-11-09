package org.opennms.horizon.inventory.mapper;

import com.vladmihalcea.hibernate.type.basic.Inet;
import org.mapstruct.Mapper;
import org.opennms.horizon.inventory.dto.IpInterfaceDTO;
import org.opennms.horizon.inventory.model.IpInterface;

@Mapper(componentModel = "spring")
public interface IpInterfaceMapper {
    IpInterface dtoToModel(IpInterfaceDTO dto);
    IpInterfaceDTO modelToDTO(IpInterface model);

    default Inet map(String value) {
        if (value == null || value.length() == 0) {
            return null;
        } else {
            return new Inet(value);
        }
    }
    default String map(Inet value) {
        if (value == null) {
            return "";
        } else {
            return value.getAddress();
        }
    }
}
