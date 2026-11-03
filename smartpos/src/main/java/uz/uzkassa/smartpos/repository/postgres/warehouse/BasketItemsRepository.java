package uz.uzkassa.smartpos.repository.postgres.warehouse;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpos.domain.postgres.warehouse.BasketItem;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface BasketItemsRepository extends JpaRepository<BasketItem, Long> {
    List<BasketItem> findByCompanyIdAndSupplyId(Long companyId, Long supplyId);

    Optional<BasketItem> findBySupplierProductIdAndCompanyId(Long supplierProductId, Long companyId);

    List<BasketItem> findByCompanyId(Long companyId);

    void deleteBySupplierProductIdAndCompanyId(Long supplierProductId, Long companyId);

    void deleteByCompanyId(Long companyId);

    void deleteAllByCompanyIdAndSupplierProductIdIn(@NotNull Long companyId, Collection<@NotNull Long> supplierProductId);
}
