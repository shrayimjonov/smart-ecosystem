package uz.uzkassa.cts.mappings;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import uz.uzkassa.cts.domain.FreePeriodHistory;
import uz.uzkassa.cts.dto.freeperiodhistory.response.FreePeriodHistoryResponseDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FreePeriodHistoryMapper {

    @Mapping(source = "creator.fullName", target = "creatorName", defaultValue = "System")
    @Mapping(source = "lastModifier.fullName", target = "lastModifierName", defaultValue = "System")
    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "customer.companyName", target = "customerName")
    @Mapping(source = "customer.tin", target = "tin")
    @Mapping(source = "customerBranchDevice.device.serialNumber", target = "deviceSerialNumber")
    @Mapping(source = "customerBranchDevice.id", target = "customerBranchDeviceId")
    FreePeriodHistoryResponseDto toDto(FreePeriodHistory entity);
}
