package uz.uzkassa.smartpos.repository.postgres.sales;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpos.domain.postgres.sales.Sales;

import java.util.Optional;

@Repository
public interface SalesRepository extends JpaRepository<Sales, Long>, JpaSpecificationExecutor<Sales> {
    Optional<Sales> findFirstByUid(String uid);

    boolean existsByFacturaId(String facturaId);
}