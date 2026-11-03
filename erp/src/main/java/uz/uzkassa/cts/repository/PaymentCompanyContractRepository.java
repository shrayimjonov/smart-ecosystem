package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.PaymentCompanyContract;
import uz.uzkassa.cts.enums.ContractStatus;

import java.util.List;

@Repository
public interface PaymentCompanyContractRepository extends BaseRepository<PaymentCompanyContract, Long> {

    List<PaymentCompanyContract> findByCompanyId(Long companyId);

    List<PaymentCompanyContract> findByCompanyIdAndStatus(Long companyId, ContractStatus status);
}
