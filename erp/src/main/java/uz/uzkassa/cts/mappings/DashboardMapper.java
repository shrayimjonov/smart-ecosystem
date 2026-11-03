package uz.uzkassa.cts.mappings;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import uz.uzkassa.cts.domain.CustomerBranchDevice;
import uz.uzkassa.cts.dto.dashboard.response.DashboardCustomerBranchDeviceDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DashboardMapper {

    @Mapping(source = "customerBranch.id", target = "customerBranchId")
    @Mapping(source = "customerBranch.name", target = "customerBranchName")
    @Mapping(source = "device.serialNumber", target = "deviceSerialNumber")
    @Mapping(source = "device.id", target = "deviceId")
    @Mapping(source = "tariff.id", target = "tariffId")
    @Mapping(source = "tariff.name", target = "tariffName")
    DashboardCustomerBranchDeviceDto toDto(CustomerBranchDevice entity);
}
