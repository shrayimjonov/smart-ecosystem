package uz.uzkassa.cts.mappings;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import uz.uzkassa.cts.domain.TradeInDevice;
import uz.uzkassa.cts.dto.tradeindevice.TradeInDeviceDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TradeInDeviceMapper {

    TradeInDeviceDTO toDto(TradeInDevice entity);
    TradeInDevice toEntity(TradeInDeviceDTO dto);
    void toEntity(TradeInDeviceDTO dto, @MappingTarget TradeInDevice entity);
}
