package uz.uzkassa.cts.mappings;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import uz.uzkassa.cts.domain.TradeInInvoice;
import uz.uzkassa.cts.dto.tradeininvoice.response.TradeInInvoiceResponseDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TradeInInvoiceMapper {

    @Mapping(source = "contract.customer.id", target = "customerId")
    @Mapping(source = "contract.customer.name", target = "customerName")
    @Mapping(source = "contract.customer.tin", target = "customerTin")
    @Mapping(source = "contract.number", target = "contractNumber")
    @Mapping(source = "contract.id", target = "contractId")
    @Mapping(source = "contract.createdDate", target = "contractDate")
    @Mapping(source = "detail.deviceSerialNumber", target = "deviceSerialNumber")
    @Mapping(source = "detail.totalProductCount", target = "totalProductCount")
    @Mapping(source = "number", target = "invoiceNumber")
    TradeInInvoiceResponseDto toDto(TradeInInvoice entity);
}
