package uz.uzkassa.cts.mappings;

import org.mapstruct.*;
import uz.uzkassa.cts.domain.RentActOfReturn;
import uz.uzkassa.cts.dto.rentactofreturn.response.RentActOfReturnResponseDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR, uses = {RentActAccessoryMapper.class})
public interface RentActOfReturnMapper {

    @Mapping(source = "customer.companyName", target = "customerName")
    @Mapping(source = "customer.tin", target = "customerTin")
    @Mapping(source = "customer.id", target = "customerId")
    RentActOfReturnResponseDto toDto(RentActOfReturn entity);

    @Mapping(source = "customer.name", target = "customerName")
    @Mapping(source = "customer.tin", target = "customerTin")
    @Mapping(source = "customer.id", target = "customerId")
    RentActOfReturnResponseDto toListDto(RentActOfReturn entity);
}
