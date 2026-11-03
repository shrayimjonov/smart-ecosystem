package uz.uzkassa.cts.mappings;

import org.mapstruct.*;
import uz.uzkassa.cts.domain.RentDeviceAct;
import uz.uzkassa.cts.dto.rentDeviceAct.RentDeviceActDTO;
import uz.uzkassa.cts.dto.rentDeviceAct.RentDeviceActListDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RentDeviceActMapper {

    @Mapping(source = "customer.companyName", target = "customerName")
    @Mapping(source = "customer.tin", target = "customerTin")
    @Mapping(source = "customer.id", target = "customerId")
    RentDeviceActDTO toDto(RentDeviceAct entity);

    @Mapping(source = "customer.name", target = "customerName")
    @Mapping(source = "customer.tin", target = "customerTin")
    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "rentDeviceActDetail.insuranceStatus", target = "insuranceStatus")
    @Mapping(target = "serialNumber", expression = "java(serialNumber(entity))")
    RentDeviceActListDTO toListDto(RentDeviceAct entity);

    @AfterMapping
    default String serialNumber(final RentDeviceAct entity) {
        if (entity.getRentDeviceActDetail() != null) {
            return entity.getRentDeviceActDetail().getDevice().getSerialNumber();
        }
        return "";
    }
}
