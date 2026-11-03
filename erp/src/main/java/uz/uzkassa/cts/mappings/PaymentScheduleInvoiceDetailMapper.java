package uz.uzkassa.cts.mappings;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import uz.uzkassa.cts.domain.PaymentScheduleInvoiceDetail;
import uz.uzkassa.cts.dto.paymentscheduleinvoice.response.PaymentScheduleInvoiceDetailResponseDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentScheduleInvoiceDetailMapper {

    @Mapping(source = "device.serialNumber", target = "deviceSerialNumber")
    PaymentScheduleInvoiceDetailResponseDto toDto(PaymentScheduleInvoiceDetail entity);
}
