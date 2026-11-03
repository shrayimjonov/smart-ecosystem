package uz.uzkassa.cts.mappings;

import org.mapstruct.*;
import uz.uzkassa.cts.domain.RentDeviceActOfReturn;
import uz.uzkassa.cts.dto.rentDeviceAct.RentDeviceActOfReturnDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR, uses = {RentActAccessoryMapper.class})
public interface RentDeviceActOfReturnMapper {

    @Mapping(source = "customer.companyName", target = "customerName")
    @Mapping(source = "customer.tin", target = "customerTin")
    @Mapping(source = "customer.id", target = "customerId")
    RentDeviceActOfReturnDTO toDto(RentDeviceActOfReturn entity);

    @Mapping(source = "customer.name", target = "customerName")
    @Mapping(source = "customer.tin", target = "customerTin")
    @Mapping(source = "customer.id", target = "customerId")
    RentDeviceActOfReturnDTO toListDto(RentDeviceActOfReturn entity);
}
