package uz.uzkassa.smartpos.repository.postgres.receipt;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpos.domain.postgres.receipt.Receipt;
import uz.uzkassa.smartpos.enums.ReceiptStatus;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long>, ReceiptRepositoryCustom {
    Page<Receipt> findByBranchIdAndStatus(Long branchId, ReceiptStatus status, Pageable pageable);

    Optional<Receipt> findOneByUid(String uid);

    boolean existsByFacturaId(String facturaId);

    List<Receipt> findAllByFacturaId(String facturaId);

    List<Receipt> findAllByTerminalIdAndStatusAndFacturaIdNull(Long terminalId, ReceiptStatus status);

    @Query("select distinct r.uid from Receipt r where r.status <> 'DRAFT' and r.uid in :uidList")
    Set<String> getAlreadyUsedUidList(@Param("uidList") Collection<String> uidList);

    @Query("select count (r.id) from Receipt r where r.status = 'DRAFT' and r.branchId = :branchId")
    Long findAllDraftsCount(@Param("branchId") Long branchId);

    @Query("select count (r.id) from Receipt r where r.status = 'DRAFT' and r.branchId in (:branchIds)")
    Long findAllDraftsCount(@Param("branchIds") Collection<Long> branchId);


    @Query("select count (r.id) from Receipt r where r.status = 'DRAFT' and r.companyId = :companyId")
    Long findAllDraftsCountByCompany(@Param("companyId") Long companyId);

    List<Receipt> findFirst1000ByStatusAndCreatedDateTimeLessThan(ReceiptStatus status, LocalDateTime createdDateTime);
}
