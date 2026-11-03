package uz.uzkassa.cts.mappings;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import uz.uzkassa.cts.domain.PaymentScheduleInvoice;
import uz.uzkassa.cts.dto.paymentscheduleinvoice.response.PaymentScheduleInvoiceResponseDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR, uses = {PaymentScheduleInvoiceDetailMapper.class})
public interface PaymentScheduleInvoiceMapper {

    PaymentScheduleInvoiceResponseDto toDto(PaymentScheduleInvoice entity);
}
