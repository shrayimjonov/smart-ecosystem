package uz.uzkassa.cts.mappings;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import uz.uzkassa.cts.domain.TradeInCounterDevicePrice;
import uz.uzkassa.cts.dto.tradeIncounterdeviceprice.request.TradeInCounterDevicePriceRequestDTO;
import uz.uzkassa.cts.dto.tradeIncounterdeviceprice.response.TradeInCounterDevicePriceResponseDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TradeInCounterDevicePriceMapper {

    TradeInCounterDevicePriceResponseDTO toDto(TradeInCounterDevicePrice entity);

    TradeInCounterDevicePrice toEntity(TradeInCounterDevicePriceRequestDTO dto);

    void toEntity(TradeInCounterDevicePriceRequestDTO dto, @MappingTarget TradeInCounterDevicePrice entity);
}
