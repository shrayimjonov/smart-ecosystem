package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.TradeInLetterOfGuarantee;
import uz.uzkassa.cts.enums.ContractStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface TradeInLetterOfGuaranteeRepository extends BaseRepository<TradeInLetterOfGuarantee, Long>  {

    List<TradeInLetterOfGuarantee> findAllTradeInLetterOfGuaranteeByCustomerIdAndStatus(Long customerId, ContractStatus status);

    Optional<TradeInLetterOfGuarantee> findByTradeInContractId(Long contractId);

}
