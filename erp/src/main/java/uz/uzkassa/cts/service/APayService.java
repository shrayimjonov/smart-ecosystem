package uz.uzkassa.cts.service;

import uz.uzkassa.cts.dto.apay.*;

public interface APayService {

    APayAddTransactionResponseDto add(APayAddTransactionRequestDto dto);

    APayAddTransactionResponseDto createBillTransaction(APayBillCreateRequestDTO dto);

    void updateBillTransaction(APayBillUpdateRequestDTO dto);

    APayPayResponseDto pay(APayPayRequestDto dto);
    APayPayResponseDto confirm(APayPayRequestDto dto);

    APayCheckResponseDto check(APayCheckRequestDto dto);

    APayResponseDTO getByBillId(String billId);

    OfdPaymentInfoResponseDTO fiscal(OfdBillFiscalRequestDTO requestDTO);

    ApayCustomerDetailDTO getCustomerByTin(String tin);
}
