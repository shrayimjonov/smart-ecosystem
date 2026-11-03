package uz.uzkassa.smartpos.repository.postgres.warehouse;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpos.domain.postgres.warehouse.IncomeOfProduct;

import java.util.Optional;

@Repository
public interface IncomeOfProductRepository extends JpaRepository<IncomeOfProduct, Long>, JpaSpecificationExecutor<IncomeOfProduct> {
    @Query(value = "select iop from IncomeOfProduct iop join fetch iop.incomeOfProductDetails iopd join fetch iopd.product where iop.id = :id")
    Optional<IncomeOfProduct> findByIdWithFetch(Long id);

    Optional<IncomeOfProduct> findByFacturaId(String facturaId);

    Optional<IncomeOfProduct> findByStockOutId(String stockOutId);

    void deleteAllByToBranchId(@NotNull Long toBranchId);
}
