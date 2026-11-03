package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.SaleAct;
import uz.uzkassa.cts.repository.custom.SaleActRepositoryCustom;

@Repository
public interface SaleActRepository extends BaseRepository<SaleAct, Long>, SaleActRepositoryCustom {

    SaleAct findBySaleContractId(Long contractId);
}
