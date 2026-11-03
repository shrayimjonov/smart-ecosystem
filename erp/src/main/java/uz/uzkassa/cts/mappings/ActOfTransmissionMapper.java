package uz.uzkassa.cts.mappings;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import uz.uzkassa.cts.domain.ActOfTransmission;
import uz.uzkassa.cts.dto.actoftransmission.response.ActOfTransmissionFullResponseDto;
import uz.uzkassa.cts.dto.actoftransmission.response.ActOfTransmissionResponseDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {ActOfTransmissionDetailMapper.class,
        ActPaymentScheduleMapper.class, ActOfTransmissionInvoiceMapper.class})
public interface ActOfTransmissionMapper {

    ActOfTransmissionResponseDto toDto(ActOfTransmission entity);

    ActOfTransmissionFullResponseDto toFullDto(ActOfTransmission entity);
}
