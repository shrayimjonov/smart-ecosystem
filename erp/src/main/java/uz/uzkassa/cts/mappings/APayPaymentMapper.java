package uz.uzkassa.cts.mappings;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import uz.uzkassa.cts.domain.APayPayment;
import uz.uzkassa.cts.dto.apaypayment.response.APayPaymentResponseDto;
import uz.uzkassa.cts.dto.onetimeorderinvoice.response.OneTimeOrderInvoice1cResponse;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface APayPaymentMapper {

    APayPaymentResponseDto toDto(APayPayment entity);

    @Mapping(source = "createdDate", target = "transactionDate", dateFormat = "yyyyMMddHHmmss")
    @Mapping(source = "tin", target = "customerTin")
    @Mapping(source = "companyName", target = "customerName")
    @Mapping(source = "accountNumber", target = "customerAccountNumber")
    @Mapping(source = "paymentType", target = "paymentType")
    OneTimeOrderInvoice1cResponse to1cDto(APayPayment entity);
}
