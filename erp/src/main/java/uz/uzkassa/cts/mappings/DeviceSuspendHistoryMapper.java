package uz.uzkassa.cts.mappings;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import uz.uzkassa.cts.domain.DeviceSuspendHistory;
import uz.uzkassa.cts.dto.devicesuspendhistory.response.DeviceSuspendHistoryResponseDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DeviceSuspendHistoryMapper {

    @Mapping(source = "tariff.name", target = "tariffName")
    @Mapping(source = "creator.fullName", target = "creatorFullName", defaultValue = "System")
    @Mapping(source = "lastModifier.fullName", target = "lastModifierFullName", defaultValue = "System")
    @Mapping(source = "contract.customer.id", target = "customerId")
    @Mapping(source = "contract.customer.companyName", target = "customerName")
    DeviceSuspendHistoryResponseDto toDto(DeviceSuspendHistory entity);
}
