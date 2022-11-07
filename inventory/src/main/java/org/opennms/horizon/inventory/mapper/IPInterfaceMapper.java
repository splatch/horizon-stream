package org.opennms.horizon.inventory.mapper;

import com.vladmihalcea.hibernate.type.basic.Inet;
import org.mapstruct.Mapper;
import org.opennms.horizon.inventory.dto.IpInterfaceDTO;
import org.opennms.horizon.inventory.model.IpInterface;

@Mapper(componentModel = "spring")
public interface IPInterfaceMapper {
    IpInterface dtoToModel(IpInterfaceDTO dto);
    IpInterfaceDTO modelToDTO(IpInterface model);

    default Inet map(String value) {
        return new Inet(value);
    }
    default String map(Inet value) {
        return value.getAddress();
    }
}
