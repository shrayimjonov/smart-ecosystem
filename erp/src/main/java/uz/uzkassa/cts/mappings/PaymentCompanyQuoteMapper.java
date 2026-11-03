package uz.uzkassa.cts.mappings;

import org.mapstruct.*;
import uz.uzkassa.cts.domain.PaymentCompanyQuote;
import uz.uzkassa.cts.dto.paymentCompanyQuote.PaymentCompanyQuoteDTO;
import uz.uzkassa.cts.dto.paymentCompanyQuote.PaymentCompanyQuoteDetailDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentCompanyQuoteMapper {

    @Mapping(source = "model.name", target = "modelName")
    @Mapping(source = "company.companyBank.bank.name", target = "bankName")
    PaymentCompanyQuoteDetailDTO toDto(PaymentCompanyQuote entity);

    PaymentCompanyQuote toEntity(PaymentCompanyQuoteDTO dto);

    PaymentCompanyQuote toEntity(PaymentCompanyQuoteDTO dto, @MappingTarget PaymentCompanyQuote entity);
}
