package uz.uzkassa.silen.repository.pg;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.uzkassa.silen.domain.Warehouse;

import java.util.Optional;

/**
 * Spring Data repository for the Warehouse entity.
 */
@SuppressWarnings("unused")
@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, String>, WarehouseRepositoryCustom {
    Optional<Warehouse> findFirstByOrganizationIdAndProductIdAndDeletedFalseOrderByCreatedDateDesc(String organizationId, String productId);
}
