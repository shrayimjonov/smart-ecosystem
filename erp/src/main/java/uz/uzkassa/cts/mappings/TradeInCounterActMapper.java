package uz.uzkassa.cts.mappings;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import uz.uzkassa.cts.domain.TradeInCounterAct;
import uz.uzkassa.cts.dto.tradeincounteract.response.TradeInCounterActResponseDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TradeInCounterActMapper {

//    @Mapping(source = "device.serialNumber", target = "deviceSerialNumber")
//    @Mapping(source = "device.model.name", target = "deviceModelName")
    @Mapping(source = "creator.login", target = "creatorName")
    @Mapping(source = "lastModifier.login", target = "modifierName")
    TradeInCounterActResponseDto toDto(TradeInCounterAct act);
}
