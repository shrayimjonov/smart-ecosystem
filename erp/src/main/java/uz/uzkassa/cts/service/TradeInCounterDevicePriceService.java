package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.TradeInCounterDevicePrice;
import uz.uzkassa.cts.dto.tradeIncounterdeviceprice.request.TradeInCounterDevicePriceRequestDTO;
import uz.uzkassa.cts.dto.tradeIncounterdeviceprice.response.TradeInCounterDevicePriceResponseDTO;
import uz.uzkassa.cts.enums.CustomerVatTypeEnum;
import uz.uzkassa.cts.enums.SmartOrOldDeviceEnum;
import uz.uzkassa.cts.filter.BaseFilter;

import java.util.List;

public interface TradeInCounterDevicePriceService extends BaseService<TradeInCounterDevicePrice, Long> {
    TradeInCounterDevicePriceResponseDTO create(TradeInCounterDevicePriceRequestDTO dto);

    TradeInCounterDevicePriceResponseDTO update(TradeInCounterDevicePriceRequestDTO dto);

    TradeInCounterDevicePriceResponseDTO getById(Long id);

    Page<TradeInCounterDevicePriceResponseDTO> list(BaseFilter filter);

    TradeInCounterDevicePriceResponseDTO findByActiveSmartOrOldDevice(SmartOrOldDeviceEnum deviceType, CustomerVatTypeEnum customerVatTypeEnum);

    List<TradeInCounterDevicePriceResponseDTO> getActiveDevices();

    List<TradeInCounterDevicePriceResponseDTO> getActiveDevicesByDeviceType(SmartOrOldDeviceEnum deviceType);

}
