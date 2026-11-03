package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.company.Partners;
import uz.uzkassa.smartpossupply.enums.CompanyStatus;

import java.util.Optional;
import java.util.Set;

@Repository
public interface PartnerRepository extends BaseRepository<Partners, Long>, PartnerRepositoryCustom {

    @Modifying
    void deleteBySupplierIdAndCustomerId(Long supplierId, Long customerId);

    boolean existsBySupplierIdAndCustomerIdAndCompanyId(Long supplierId, Long customerId, Long companyId);

    boolean existsByCustomerIdAndStatus(Long customerId, CompanyStatus status);

    @Query("select p.supplierId from Partners p")
    Set<Long> findAllSuppliers();

    @Query("select p.customerId from Partners p")
    Set<Long> findAllCustomers();

    Optional<Partners> findByCompanyIdAndCustomerIdAndSupplierId(Long companyId, Long customerId, Long supplierId);

    @Query("""
            SELECT count(p.customerId) FROM Partners p
                        JOIN Company c ON p.customerId = c.id
            where p.companyId = :companyId AND p.supplierId = :companyId AND p.status =:status AND c.deleted=false
            """)
    Long countDistinctByCompanyIdAndStatus(Long companyId, CompanyStatus status);

    Optional<Partners> findFirstByExternalIdAndDeletedIsFalse(String externalId);
}
