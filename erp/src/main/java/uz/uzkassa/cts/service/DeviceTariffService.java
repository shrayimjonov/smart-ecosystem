package uz.uzkassa.cts.service;

import uz.uzkassa.cts.domain.DeviceTariff;
import uz.uzkassa.cts.dto.launcher.LauncherNotification;
import uz.uzkassa.cts.dto.launcher.LauncherRabbitPaginationRequestDto;

import java.time.LocalDate;

public interface DeviceTariffService extends BaseService<DeviceTariff, Long> {
    void switchDevices(LocalDate localDate);

    void notifyDevices(LauncherNotification notification);

    void blockUnblockDevices();

    void blockUnblockDevices(LauncherRabbitPaginationRequestDto dto);
}
