package uz.uzkassa.cts.service;

import uz.uzkassa.cts.domain.KapitalbankPayment;
import uz.uzkassa.cts.dto.kapitalbank.request.*;
import uz.uzkassa.cts.dto.kapitalbank.response.*;

public interface KapitalBankService {

    TransactionCreateResponseDto createTransaction(TransactionCreateRequestDto dto);

    PayResponseDto pay(PayRequestDto dto);

    InfoResponseDto getInfo(InfoRequestDto dto);

    CheckStatusResponseDto check(CheckStatusRequestDto dto);

    CustomerCheckResponseDto getClientInfoByDeviceSerial(String serialNumber);

    TransactionCreateResponseDto createPayment(CreatePaymentRequestDto dto);

    void acceptPayment(PayRequestDto dto);

    void checkPaymentStatus(KapitalbankPayment payment);

    CustomerCheckResponseDto getClientInfoByTin(String tin);
}
