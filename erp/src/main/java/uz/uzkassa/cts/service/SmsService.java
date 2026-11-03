package uz.uzkassa.cts.service;

import uz.uzkassa.cts.dto.sms.EtcRequestDTO;

public interface SmsService {

    boolean send(EtcRequestDTO etcRequestDTO);

    void checkConnection();
}
