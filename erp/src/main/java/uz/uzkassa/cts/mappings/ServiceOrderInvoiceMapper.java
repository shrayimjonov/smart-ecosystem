package uz.uzkassa.cts.mappings;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import uz.uzkassa.cts.domain.ServiceOrderInvoice;
import uz.uzkassa.cts.dto.serviceorderinvoice.response.ServiceOrderInvoiceListDto;
import uz.uzkassa.cts.dto.serviceorderinvoice.response.ServiceOrderInvoiceResponseDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ServiceOrderInvoiceMapper {

    ServiceOrderInvoiceResponseDto toDto(ServiceOrderInvoice invoice);

    @Mapping(source = "contract.customer.companyName", target = "customerName")
    @Mapping(source = "contract.customer.tin", target = "tin")
    @Mapping(source = "contract.name", target = "contractNumber")
    @Mapping(source = "contract.docDate", target = "contractDate")
    @Mapping(source = "number", target = "invoiceNumber")
    @Mapping(source = "totalPrice", target = "totalPrice")
    @Mapping(source = "totalVatPrice", target = "totalVat")
    @Mapping(source = "totalSellPrice", target = "amount")
    ServiceOrderInvoiceListDto toListDto(ServiceOrderInvoice invoice);
}
