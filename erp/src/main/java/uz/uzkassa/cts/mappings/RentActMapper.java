package uz.uzkassa.cts.mappings;

import org.mapstruct.*;
import uz.uzkassa.cts.domain.RentAct;
import uz.uzkassa.cts.dto.rentact.response.RentActListResponseDto;
import uz.uzkassa.cts.dto.rentact.response.RentActResponseDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR, uses = {RentActAccessoryMapper.class})
public interface RentActMapper {

    @Mapping(source = "customer.companyName", target = "customerName")
    @Mapping(source = "customer.tin", target = "customerTin")
    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "offer.number", target = "offerNumber")
    RentActResponseDto toDto(RentAct entity);

    @Mapping(source = "customer.name", target = "customerName")
    @Mapping(source = "customer.tin", target = "customerTin")
    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "offer.number", target = "offerNumber")
    RentActListResponseDto toListDto(RentAct entity);
}
