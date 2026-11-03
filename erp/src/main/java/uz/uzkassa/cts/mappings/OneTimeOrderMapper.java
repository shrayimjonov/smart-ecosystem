package uz.uzkassa.cts.mappings;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import uz.uzkassa.cts.domain.OneTimeOrder;
import uz.uzkassa.cts.dto.onetimeorder.response.OneTimeOrderResponseDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OneTimeOrderMapper {

    OneTimeOrder toEntity(OneTimeOrderResponseDto dto);

    @Mapping(source = "customer.name", target = "customerName")
    @Mapping(source = "customer.tin", target = "tin")
    @Mapping(source = "service.name", target = "serviceName")
    @Mapping(source = "contract.name", target = "contractName")
    @Mapping(source = "creator.fullName", target = "creatorFullName")
    @Mapping(source = "lastModifier.fullName", target = "lastModifierFullName")
    OneTimeOrderResponseDto toDto(OneTimeOrder entity);
}
