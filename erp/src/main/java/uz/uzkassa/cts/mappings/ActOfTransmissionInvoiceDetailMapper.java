package uz.uzkassa.cts.mappings;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import uz.uzkassa.cts.domain.ActOfTransmissionInvoiceDetail;
import uz.uzkassa.cts.dto.actoftransmissioninvoice.response.ActOfTransmissionInvoiceDetailResponseDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ActOfTransmissionInvoiceDetailMapper {

    @Mapping(source = "device.serialNumber", target = "deviceSerialNumber")
    ActOfTransmissionInvoiceDetailResponseDto toDto(ActOfTransmissionInvoiceDetail entity);
}
