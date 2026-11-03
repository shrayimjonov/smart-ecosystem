package uz.uzkassa.silen.repository.pg;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.uzkassa.silen.domain.PurchaseOrder;

/**
 * Spring Data repository for the PurchaseOrder entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long>, PurchaseOrderRepositoryCustom {
    @Query("select max(intNumber) from PurchaseOrder where fromOrganizationId=?1")
    Integer getMaxIntNumber(String fromOrganizationId);

    boolean existsPurchaseOrderByFromOrganizationIdAndNumberIgnoreCase(String organizationId, String number);

    boolean existsPurchaseOrderByIdNotAndFromOrganizationIdAndStoreIdAndNumberIgnoreCase(Long id, String organizationId, Long storeId, String number);

    boolean existsPurchaseOrderByIdNotAndFromOrganizationIdAndNumberIgnoreCase(Long id, String organizationId, String number);
}
