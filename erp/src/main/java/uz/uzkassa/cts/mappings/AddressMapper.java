package uz.uzkassa.cts.mappings;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import uz.uzkassa.cts.domain.Address;
import uz.uzkassa.cts.domain.address.BaseAddress;
import uz.uzkassa.cts.dto.address.AddressDetailDTO;
import uz.uzkassa.cts.dto.address.AddressListDTO;
import uz.uzkassa.cts.dto.address.base.BaseAddressDTO;
import uz.uzkassa.cts.dto.address.base.BaseAddressDetailDTO;
import uz.uzkassa.cts.dto.address.base.AddressShortDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AddressMapper {

    BaseAddressDTO toDTO(BaseAddress entity);

    AddressShortDTO toShortDTO(BaseAddress entity);

    BaseAddressDetailDTO toDetailDTO(BaseAddress entity);

    BaseAddress toEntity(BaseAddressDTO dto);

    BaseAddress merge(BaseAddressDTO dto, @MappingTarget BaseAddress entity);

    AddressShortDTO toShortDTO(Address address);

    AddressDetailDTO toDetailDTO(Address address);

    AddressListDTO toListDTO(Address address);
}

