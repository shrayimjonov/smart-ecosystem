package uz.uzkassa.cts.service;

import uz.uzkassa.cts.dto.cron.CronDeviceSwitchDto;

import java.time.LocalDate;
import java.util.List;

public interface CronService {
    List<CronDeviceSwitchDto> getCustomerDevicesForStatusChange(LocalDate localDate);

    List<CronDeviceSwitchDto> getCustomerDevicesForInvoice(LocalDate localDate);
}
