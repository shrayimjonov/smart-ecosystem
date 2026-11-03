package uz.uzkassa.cts.mappings;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import uz.uzkassa.cts.domain.CtsServiceOrderSpecification;
import uz.uzkassa.cts.dto.orderspecification.response.OrderSpecificationResponseDto;
import uz.uzkassa.cts.dto.orderspecification.response.ServiceOrderSpecificationListDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ServiceOrderSpecificationMapper {

    CtsServiceOrderSpecification toEntity(OrderSpecificationResponseDto dto);

    OrderSpecificationResponseDto toDto(CtsServiceOrderSpecification dto);

    @Mapping(source = "contract.customer.tin", target = "tin")
    @Mapping(source = "contract.customer.name", target = "customerName")
    @Mapping(source = "contract.customer.id", target = "customerId")
    @Mapping(source = "contract.id", target = "contractId")
    @Mapping(source = "contract.name", target = "contractNumber")
    @Mapping(source = "contract.createdDate", target = "contractDate")
    @Mapping(source = "number", target = "specificationNumber")
    @Mapping(source = "createdDate", target = "specificationDate")
    @Mapping(source = "totalSellPrice", target = "totalPrice")
    @Mapping(source = "totalVatPrice", target = "totalVat")
    @Mapping(source = "totalPrice", target = "amount")
    ServiceOrderSpecificationListDto toListDto(CtsServiceOrderSpecification dto);
}
