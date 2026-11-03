package uz.uzkassa.cts.mappings;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import uz.uzkassa.cts.domain.ServicePaymentHistory;
import uz.uzkassa.cts.dto.servicepaymenthistory.response.ServicePaymentHistoryResponseDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ServicePaymentHistoryMapper {

    @Mapping(source = "tariff.name", target = "tariffName")
    ServicePaymentHistoryResponseDto toDto(ServicePaymentHistory entity);
}
