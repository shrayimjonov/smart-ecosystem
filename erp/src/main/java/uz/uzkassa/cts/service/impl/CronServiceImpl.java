package uz.uzkassa.cts.service.impl;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import uz.uzkassa.cts.dto.cron.CronDeviceSwitchDto;
import uz.uzkassa.cts.repository.custom.CronRepositoryCustom;
import uz.uzkassa.cts.service.CronService;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class CronServiceImpl implements CronService {

    private final CronRepositoryCustom cronRepositoryCustom;

    public CronServiceImpl(CronRepositoryCustom cronRepositoryCustom) {
        this.cronRepositoryCustom = cronRepositoryCustom;
    }

    private List<CronDeviceSwitchDto> getCustomerDeviceList(boolean isInvoice, LocalDate localDate) {
        return cronRepositoryCustom.getCustomerDeviceList(isInvoice, localDate);
    }

    @Override
    public List<CronDeviceSwitchDto> getCustomerDevicesForStatusChange(LocalDate localDate) {
        return getCustomerDeviceList(false, localDate);
    }

    @Override
    public List<CronDeviceSwitchDto> getCustomerDevicesForInvoice(LocalDate localDate) {
        return getCustomerDeviceList(true, localDate);
    }
}
