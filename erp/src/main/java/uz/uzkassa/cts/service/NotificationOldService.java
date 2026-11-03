package uz.uzkassa.cts.service;

import uz.uzkassa.cts.dto.launcher.LauncherNotification;
import uz.uzkassa.cts.dto.sms.EtcRequestDTO;

public interface NotificationOldService {

    void notifyAllDevices(LauncherNotification notification);

    void notifyAboutMonthlyServiceFee(String tin);

    void notifyAboutMonthlyServiceFeeBySms(String tin);

    void notifyBySms(String phoneNumber);

    void notifyBySms(String phoneNumber, String text);

    void notifyBySms(EtcRequestDTO dto);
}
