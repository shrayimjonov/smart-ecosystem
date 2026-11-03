package uz.uzkassa.cts.mappings;

import org.mapstruct.*;
import uz.uzkassa.cts.domain.TradeInPriceHistory;
import uz.uzkassa.cts.dto.tradeinpricehistory.request.TradeInPriceHistoryRequestDTO;
import uz.uzkassa.cts.dto.tradeinpricehistory.response.TradeInPriceHistoryResponseDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TradeInPriceHistoryMapper {

    @Mapping(source = "deviceModel.name", target = "deviceModelName")
    TradeInPriceHistoryResponseDTO toDto(TradeInPriceHistory entity);

    TradeInPriceHistory toEntity(TradeInPriceHistoryRequestDTO dto);

    void toEntity(TradeInPriceHistoryRequestDTO dto, @MappingTarget TradeInPriceHistory entity);
}
