package uz.uzkassa.cts.mappings;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import uz.uzkassa.cts.domain.SaleInvoice;
import uz.uzkassa.cts.dto.sale.invoice.SaleInvoiceResponseDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SaleInvoiceMapper {

//    @Mapping(source = "detail.deviceSerialNumber", target = "deviceSerialNumber")
//    @Mapping(source = "detail.totalProductCount", target = "totalProductCount")

    @Mapping(source = "saleContract.customer.id", target = "customerId")
    @Mapping(source = "saleContract.customer.name", target = "customerName")
    @Mapping(source = "saleContract.customer.tin", target = "customerTin")
    @Mapping(source = "saleContract.number", target = "contractNumber")
    @Mapping(source = "saleContract.id", target = "saleContractId")
    @Mapping(source = "saleContract.createdDate", target = "contractDate")
    @Mapping(source = "number", target = "invoiceNumber")
    SaleInvoiceResponseDto toDto(SaleInvoice entity);
}
