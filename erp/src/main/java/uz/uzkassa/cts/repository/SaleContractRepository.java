package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.SaleContract;
import uz.uzkassa.cts.repository.custom.SaleContractRepositoryCustom;

import java.util.List;

@Repository
public interface SaleContractRepository extends BaseRepository<SaleContract, Long>, SaleContractRepositoryCustom {

    List<SaleContract> findByCustomerId(Long customerId);
}
