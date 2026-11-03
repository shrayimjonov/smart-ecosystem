package uz.uzkassa.cts.mappings;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import uz.uzkassa.cts.domain.KapitalbankPayment;
import uz.uzkassa.cts.dto.kapitalbankpayment.response.KapitalbankPaymentResponseDto;
import uz.uzkassa.cts.dto.onetimeorderinvoice.response.OneTimeOrderInvoice1cResponse;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface KapitalbankPaymentMapper {

    //    @Mapping(source = "pan", target = "pan", expression = "java(entity.getPan.replace)")
    KapitalbankPaymentResponseDto toDto(KapitalbankPayment entity);

    @AfterMapping
    // or @BeforeMapping
    default void replacePan(KapitalbankPayment entity, @MappingTarget KapitalbankPaymentResponseDto dto) {
        if (StringUtils.isEmpty(entity.getPan()))
            return;
        String pan = entity.getPan().substring(0, 6) +
                "******" +
                entity.getPan().substring(12, 16);
        dto.setPan(pan);
    }

    @Mapping(source = "createdDate", target = "transactionDate", dateFormat = "yyyyMMddHHmmss")
    @Mapping(source = "tin", target = "customerTin")
    @Mapping(source = "companyName", target = "customerName")
    @Mapping(source = "accountNumber", target = "customerAccountNumber")
    OneTimeOrderInvoice1cResponse to1cDto(KapitalbankPayment entity);
}
