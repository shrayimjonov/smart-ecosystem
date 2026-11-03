package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.APayPayment;
import uz.uzkassa.cts.dto.apaypayment.request.*;
import uz.uzkassa.cts.dto.apaypayment.response.APayPaymentResponseDto;
import uz.uzkassa.cts.dto.onetimeorderinvoice.response.OneTimeOrderInvoice1cResponse;
import uz.uzkassa.cts.filter.APayPaymentFilter;
import uz.uzkassa.cts.dto.apay.APayAddTransactionResponseDto;

import java.math.BigDecimal;

public interface APayPaymentService extends BaseService<APayPayment, Long> {

    APayAddTransactionResponseDto createTransaction(APayPaymentAddRequestDto dto);

    void acceptPayment(APayPaymentPayRequestDto dto);

    void checkPayment(APayPayment payment);

    Page<APayPaymentResponseDto> list(APayPaymentFilter filter);

    byte[] download(APayPaymentFilter filter);

    BigDecimal getConfirmedAmount(APayPaymentFilter filter);

    Page<OneTimeOrderInvoice1cResponse> list1c(APayPaymentFilter filter);

    APayPaymentResponseDto changeStatus(APayChangeStatusRequestDto dto);

    APayAddTransactionResponseDto createTransactionFromApp(APayFromAppRequestDto dto);
    APayAddTransactionResponseDto createQRTransaction(APayPaymentQRAddRequestDto dto);

    void checkPaymentStatusCron();

}
