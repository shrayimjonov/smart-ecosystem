package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.TradeInDevice;
import uz.uzkassa.cts.dto.tradeindevice.TradeInDeviceDTO;
import uz.uzkassa.cts.filter.TradeInDeviceListFilter;

public interface TradeInDeviceService extends BaseService<TradeInDevice, Long> {

    TradeInDeviceDTO create(TradeInDeviceDTO tradeInDeviceDTO);

    TradeInDeviceDTO update(TradeInDeviceDTO tradeInDeviceDTO);

    TradeInDeviceDTO getById(Long id);

    Page<TradeInDeviceDTO> list(TradeInDeviceListFilter filter);
}
