package uz.uzkassa.smartpos.service;

import uz.uzkassa.smartpos.dto.etcsms.request.EtcRequestDto;
import uz.uzkassa.smartpos.enums.SmsStatusEnum;

public interface EtcSmsService {
    SmsStatusEnum sendSms(EtcRequestDto requestDto);

    void sendSmsForReceipt(EtcRequestDto requestDto);

    void saveSms(String phone, String text, SmsStatusEnum status);
}
