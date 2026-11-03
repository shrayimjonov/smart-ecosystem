package uz.uzkassa.cts.repository.custom;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.dto.cron.CronDeviceSwitchDto;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CronRepositoryCustom {

    List<CronDeviceSwitchDto> getCustomerDeviceList(boolean isInvoice, LocalDate localDate);
}
