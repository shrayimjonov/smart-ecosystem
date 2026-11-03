package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.KapitalbankPayment;
import uz.uzkassa.cts.dto.kapitalbankpayment.response.KapitalbankPaymentResponseDto;
import uz.uzkassa.cts.dto.onetimeorderinvoice.response.OneTimeOrderInvoice1cResponse;
import uz.uzkassa.cts.filter.KapitalBankFilter;

import java.math.BigDecimal;

public interface KapitalbankPaymentService extends BaseService<KapitalbankPayment, Long> {
    Page<KapitalbankPaymentResponseDto> list(KapitalBankFilter filter);

    byte[] getDownloadUrl(KapitalBankFilter filter);

    BigDecimal getConfirmedAmount(KapitalBankFilter filter);

    Page<OneTimeOrderInvoice1cResponse> list1c(KapitalBankFilter filter);
}
