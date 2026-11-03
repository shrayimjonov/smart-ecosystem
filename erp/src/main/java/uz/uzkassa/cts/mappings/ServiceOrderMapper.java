package uz.uzkassa.cts.mappings;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import uz.uzkassa.cts.domain.CtsServiceOrder;
import uz.uzkassa.cts.dto.order.request.OrderDto;
import uz.uzkassa.cts.dto.serviceorder.response.ServiceOrderListResponseDto;
import uz.uzkassa.cts.dto.serviceorder.response.ServiceOrderResponseDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ServiceOrderMapper {

    CtsServiceOrder toEntity(OrderDto dto);

    @Mapping(source = "customer.name", target = "customerName")
    @Mapping(source = "customer.tin", target = "tin")
    @Mapping(source = "serviceOrderSpecification.id", target = "specificationId")
    ServiceOrderResponseDto toDto(CtsServiceOrder dto);

    @Mapping(source = "customer.name", target = "customerName")
    @Mapping(source = "customer.tin", target = "tin")
    @Mapping(source = "serviceOrderSpecification.id", target = "specificationId")
    @Mapping(source = "creator.fullName", target = "creatorFullName")
    ServiceOrderListResponseDto toListDto(CtsServiceOrder dto);
}
