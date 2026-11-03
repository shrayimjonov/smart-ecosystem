package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.PaymentCompanyQuote;
import uz.uzkassa.cts.dto.paymentCompanyQuote.PaymentCompanyQuoteDTO;
import uz.uzkassa.cts.dto.paymentCompanyQuote.PaymentCompanyQuoteDetailDTO;
import uz.uzkassa.cts.filter.BaseFilter;

import java.util.List;

public interface PaymentCompanyQuoteService extends BaseService<PaymentCompanyQuote, Long> {

    Long create(PaymentCompanyQuoteDTO paymentCompanyQuoteDTO);

    PaymentCompanyQuoteDetailDTO get(Long id);

    Page<PaymentCompanyQuoteDetailDTO> list(BaseFilter filter);

    String generateHtmlTemplate(Long id);

    List<PaymentCompanyQuoteDetailDTO> paymentCompanyQuoteListByTin(String tin);
}
