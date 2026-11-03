package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.CompetitorProduct;

import java.util.List;

@Repository
public interface CompetitorProductRepository extends BaseRepository<CompetitorProduct, Long> {

    List<CompetitorProduct> findAllByCustomerBranchId(Long customerBranchId);
}
