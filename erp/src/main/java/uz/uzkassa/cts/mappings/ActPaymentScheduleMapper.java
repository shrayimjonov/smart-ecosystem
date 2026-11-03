package uz.uzkassa.cts.mappings;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import uz.uzkassa.cts.domain.ActPaymentSchedule;
import uz.uzkassa.cts.dto.actoftransmission.response.ActPaymentScheduleResponseDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ActPaymentScheduleMapper {

    ActPaymentScheduleResponseDto toDto(ActPaymentSchedule entity);
}
