package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.KapitalLegalPaymentDetail;
import uz.uzkassa.cts.dto.kapitallegal.response.KapitalLegalResponseDto;
import uz.uzkassa.cts.filter.KapitalLegalFilter;

import java.math.BigDecimal;
import java.util.List;

public interface KapitalLegalPaymentDetailService extends BaseService<KapitalLegalPaymentDetail, Long> {
    Page<KapitalLegalResponseDto> list(KapitalLegalFilter filter);

    BigDecimal getAmount(KapitalLegalFilter filter);

    void sendToBilling(List<Long> ids);

    void sentToBillingCronJob();
}
