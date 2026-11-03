package uz.uzkassa.silen.repository.pg;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.uzkassa.silen.domain.Stock;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data repository for the Stock entity.
 */
@SuppressWarnings("unused")
@Repository
public interface StockRepository extends JpaRepository<Stock, String>, StockRepositoryCustom {
    Optional<Stock> findFirstByOrganizationIdAndProductIdAndDeletedFalse(String organizationId, String productId);

    List<Stock> findAllByOrganizationIdAndDeletedFalse(String organizationId);
}
