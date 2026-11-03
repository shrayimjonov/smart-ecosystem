package uz.uzkassa.horeca.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;
import uz.uzkassa.horeca.domain.address.Address;
import uz.uzkassa.horeca.dto.address.AddressDTO;
import uz.uzkassa.horeca.dto.address.AddressDetailDTO;

@Component
@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface AddressMapper extends BaseMapper<Address, AddressDTO, AddressDetailDTO> {}
