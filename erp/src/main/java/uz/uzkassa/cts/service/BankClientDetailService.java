package uz.uzkassa.cts.service;

import uz.uzkassa.cts.domain.BankClientDetail;
import uz.uzkassa.cts.dto.bankclient.response.BankClientDetailsDto;

public interface BankClientDetailService extends BaseService<BankClientDetail, Long> {
    BankClientDetailsDto approve(Long id);
}
