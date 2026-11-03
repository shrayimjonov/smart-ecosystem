package uz.uzkassa.cts.mappings;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import uz.uzkassa.cts.domain.TradeInAct;
import uz.uzkassa.cts.dto.tradeinact.response.TradeInActResponseDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TradeInActMapper {

    @Mapping(source = "device.serialNumber", target = "deviceSerialNumber")
    @Mapping(source = "device.model.name", target = "deviceModelName")
    @Mapping(source = "creator.login", target = "creatorName")
    @Mapping(source = "lastModifier.login", target = "modifierName")
    TradeInActResponseDto toDto(TradeInAct act);
}
