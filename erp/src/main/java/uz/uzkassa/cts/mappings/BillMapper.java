package uz.uzkassa.cts.mappings;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import uz.uzkassa.cts.domain.Bill;
import uz.uzkassa.cts.dto.bill.BillDetailDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BillMapper {

    @Mapping(source = "device.serialNumber", target = "deviceSerialNumber")
    @Mapping(source = "customerBranchDeviceId", target = "customerBranchDeviceId")
    BillDetailDTO toDto(Bill entity);
}
