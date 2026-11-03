package uz.uzkassa.cts.service;

import uz.uzkassa.cts.domain.PaymentContractDetail;
import uz.uzkassa.cts.dto.payment.PaymentContractActDTO;
import uz.uzkassa.cts.dto.payment.PaymentContractDetailDTO;

public interface PaymentContractDetailService extends BaseService<PaymentContractDetail, Long> {

    void create(PaymentContractDetailDTO detailDTO);

    void generateAct(PaymentContractActDTO dto);
}
