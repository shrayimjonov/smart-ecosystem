package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.TradeInInvoice;
import uz.uzkassa.cts.repository.custom.TradeInInvoiceRepositoryCustom;

import java.util.List;
import java.util.Optional;

@Repository
public interface TradeInInvoiceRepository extends BaseRepository<TradeInInvoice, Long>, TradeInInvoiceRepositoryCustom {

    TradeInInvoice findByContract_Id(Long contractId);

    List<TradeInInvoice> findAllByXfileIdIsNull();

    Optional<TradeInInvoice> findByTradeInAct_IdAndContract_Id(Long tradeInActId, Long tradeInContractId);

    TradeInInvoice findByXfileId(String xfileId);
}
