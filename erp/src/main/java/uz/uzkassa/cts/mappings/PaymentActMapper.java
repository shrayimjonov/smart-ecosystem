package uz.uzkassa.cts.mappings;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import uz.uzkassa.cts.domain.PaymentAct;
import uz.uzkassa.cts.dto.payment.PaymentActDetailDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentActMapper {

    @Mapping(source = "device.serialNumber", target = "deviceSerialNumber")
    @Mapping(source = "device.model.name", target = "deviceModelName")
    @Mapping(source = "creator.fullName", target = "creatorName")
    @Mapping(source = "lastModifier.fullName", target = "modifierName")
    PaymentActDetailDTO toDto(PaymentAct entity);
}
