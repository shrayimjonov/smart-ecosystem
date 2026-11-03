package uz.uzkassa.cts.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.TradeInCounterDevicePrice;
import uz.uzkassa.cts.enums.CustomerVatTypeEnum;
import uz.uzkassa.cts.enums.SmartOrOldDeviceEnum;

import java.util.List;

@Repository
public interface TradeInCounterDevicePriceRepository extends BaseRepository<TradeInCounterDevicePrice, Long> {

    @Query(value = "Select t from TradeInCounterDevicePrice t where t.smartOrOldDeviceEnum = :enum and t.active = true and t.customerVatTypeEnum = :vatTypeEnum")
    TradeInCounterDevicePrice findByActiveSmartOrOldDevice(@Param("enum") SmartOrOldDeviceEnum smartOrOldDeviceEnum, @Param("vatTypeEnum") CustomerVatTypeEnum customerVatTypeEnum);

    @Query(value = "Select t from TradeInCounterDevicePrice t where t.active = true")
    List<TradeInCounterDevicePrice> getActiveDevices();

    @Query(value = "Select t from TradeInCounterDevicePrice t where t.smartOrOldDeviceEnum = :enum and t.active = true")
    List<TradeInCounterDevicePrice> getActiveDevicesByDeviceType(@Param("enum") SmartOrOldDeviceEnum smartOrOldDeviceEnum);

}
