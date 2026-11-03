package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.BankClientDetailRejected;
import uz.uzkassa.cts.dto.bankclientdetailrejected.request.BankClientDetailsRejectEditRequestDto;
import uz.uzkassa.cts.dto.bankclientdetailrejected.response.BankClientDetailRejectedResponseDto;
import uz.uzkassa.cts.filter.BankClientDetailRejectedFilter;

import java.util.List;

public interface BankClientDetailRejectedService extends BaseService<BankClientDetailRejected, Long> {
    Page<BankClientDetailRejectedResponseDto> list(BankClientDetailRejectedFilter filter);
    void resend(List<Long> ids);
    void changeStatus(Long id);
    void deleteIfStatusPending(Long id);
    void updateInn(BankClientDetailsRejectEditRequestDto dto);
    byte[] download(BankClientDetailRejectedFilter filter);
}
