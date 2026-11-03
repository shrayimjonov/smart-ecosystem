package uz.uzkassa.cts.mappings;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import uz.uzkassa.cts.domain.CustomerBranchDevice;
import uz.uzkassa.cts.dto.customerbranchdevicechangetariff.CustomerBranchDeviceListDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CustomerBranchDeviceMapper {

    @Mapping(source = "customerBranch.customerId", target = "customerId")
    @Mapping(source = "customerBranch.customer.tin", target = "tin")
    @Mapping(source = "customerBranch.id", target = "branchId")
    @Mapping(source = "customerBranch.name", target = "branchName")
    @Mapping(source = "customerBranch.customer.companyName", target = "customerName")
    @Mapping(source = "device.serialNumber", target = "deviceSerialNumber")
    @Mapping(source = "id", target = "customerBranchDeviceId")
    @Mapping(source = "tariff.id", target = "tariffId")
    @Mapping(source = "tariff.name", target = "tariffName")
    CustomerBranchDeviceListDto toLstDto(CustomerBranchDevice entity);
}
