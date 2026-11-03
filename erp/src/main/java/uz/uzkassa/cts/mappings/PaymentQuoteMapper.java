package uz.uzkassa.cts.mappings;

import org.mapstruct.*;
import uz.uzkassa.cts.domain.PaymentQuote;
import uz.uzkassa.cts.dto.paymentQuote.PaymentQuoteDTO;
import uz.uzkassa.cts.dto.paymentQuote.PaymentQuoteDetailDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentQuoteMapper {

    @Mapping(source = "model.name", target = "modelName")
    @Mapping(source = "creator.fullName", target = "creatorName")
    PaymentQuoteDetailDTO toDto(PaymentQuote entity);

    PaymentQuote toEntity(PaymentQuoteDTO dto);

    PaymentQuote toEntity(PaymentQuoteDTO dto, @MappingTarget PaymentQuote entity);
}
