package uz.uzkassa.cts.service;


import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.ActOfTransmissionDetail;
import uz.uzkassa.cts.dto.actoftransmissiondetail.response.ActOfTransmissionDetailListResponseDto;
import uz.uzkassa.cts.filter.ActOfTransmissionDetailFilter;

import java.math.BigDecimal;

public interface ActOfTransmissionDetailService extends BaseService<ActOfTransmissionDetail, Long> {


    Page<ActOfTransmissionDetailListResponseDto> getList(ActOfTransmissionDetailFilter filter);

    BigDecimal getCurrentMonthIncomeExpectation();

    BigDecimal getTotalIncome(ActOfTransmissionDetailFilter filter);

    String getDebtors();

    byte[] download(ActOfTransmissionDetailFilter filter);
}
