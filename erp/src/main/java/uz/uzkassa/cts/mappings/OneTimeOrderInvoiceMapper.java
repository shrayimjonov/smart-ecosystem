package uz.uzkassa.cts.mappings;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import uz.uzkassa.cts.domain.OneTimeOrderInvoice;
import uz.uzkassa.cts.dto.onetimeorderinvoice.response.OneTimeOrderInvoiceResponseDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OneTimeOrderInvoiceMapper {

    OneTimeOrderInvoice toEntity(OneTimeOrderInvoiceResponseDto dto);

    @Mapping(source = "customer.name", target = "customerName")
    @Mapping(source = "customer.tin", target = "tin")
    @Mapping(source = "service.name", target = "serviceName")
    @Mapping(source = "contract.name", target = "contractNumber")
    @Mapping(source = "contract.docDate", target = "contractDate")
    @Mapping(source = "number", target = "invoiceNumber")
    @Mapping(source = "order.id", target = "orderId")
    OneTimeOrderInvoiceResponseDto toDto(OneTimeOrderInvoice entity);
}
