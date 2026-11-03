package uz.uzkassa.smartpossupply.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.enums.CompanyStatus;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface CompanyRepository extends BaseRepository<Company, Long>, CompanyRepositoryCustom {

    @EntityGraph(attributePaths = {"activityType", "activityType.parent", "address", "companyBank", "logo", "backgroundImage", "owner", "manager"})
    @Query("select c from Company c where c.id=:id")
    Optional<Company> getDetail(@Param("id") Long id);

    @EntityGraph(attributePaths = {"companyBank", "companyBank.bank", "companyBank.bank.parent"})
    @Query("select c from Company c where c.id=:id")
    Optional<Company> getCompanyWithBankById(Long id);

    @Cacheable(cacheNames = COMPANY_BY_TIN_CACHE)
    Optional<Company> findByTinAndDeletedIsFalse(String tin);

    Optional<Company> findByIdAndDeletedIsFalse(Long id);

    @Query("select c.id from Company c where c.deleted = false and c.erpId=:erpId")
    Long getIdByErpId(@Param("erpId") Long erpId);

    @Query("select c.id from Company c where c.tin in (:tins)")
    List<Long> findAllByTinInAndDeletedIsFalse(@Param("tins") Set<String> tins);

    Optional<Company> findFirstByTinAndDeletedIsFalse(String tin);

    List<Company> findAllByIdInAndDeletedIsFalse(Set<Long> ids);

    @Query("select c.id from Company c where c.deleted= false and c.ownerId is not null and c.tin is not null")
    List<Long> getIdsToSync();

    Optional<Company> findFirstByTinAndStatusAndDeletedIsFalse(String tin, CompanyStatus status);

    @Query("SELECT c FROM Company c WHERE c.id = :id and c.deleted = false ")
    @EntityGraph(attributePaths = {"activityType", "address", "owner"})
    Optional<Company> findByIdToSync(@Param("id") Long id);

    @Query("SELECT c FROM Company c WHERE c.id = :id")
    @EntityGraph(attributePaths = {"owner"})
    Optional<Company> findByIdWithOwner(@Param("id") Long id);

    @Query("SELECT c FROM Company c WHERE c.id = :id and c.backgroundImage is not null ")
    @EntityGraph(attributePaths = {"backgroundImage"})
    Optional<Company> findByIdWithBackgroundImage(@Param("id") Long id);

    @Query("SELECT c FROM Company c WHERE c.id = :id and c.logo is not null ")
    @EntityGraph(attributePaths = {"logo"})
    Optional<Company> findByIdWithLogo(@Param("id") Long id);

    boolean existsCompanyByTinAndIdNotAndDeletedIsFalse(String tin, Long id);

    Optional<Company> findFirstByNameIgnoreCaseAndDeletedIsFalse(String name);

    @Query(value = "select c.* from company c where c.deleted=false order by c.id limit :limitCount offset :offsetIndex", nativeQuery = true)
    List<Company> findAllByLimit(@Param("limitCount") Integer limitCount, @Param("offsetIndex") Integer offsetIndex);

    @Modifying
    @Query(value = "UPDATE Company c SET c.equipmentCount = c.equipmentCount + :equipmentCount WHERE c.deleted = FALSE AND c.id = :id")
    void updateEquipmentCount(@Param("id") Long id, @Param("equipmentCount") int equipmentCount);

    Optional<Company> findByMobileId(String mobileId);

    @Query("""
                select c.id from Company c
                join Partners p on p.customerId = c.id
                where p.supplierId = :supplierId
                  and c.telegramUserId = :telegramUserId
                  and c.deleted = false
                  and p.deleted = false
            """)
    Long findFirstByTelegramIdAndSupplierId(@Param("supplierId") Long supplierId, @Param("telegramUserId") Long telegramUserId);

    @Query("""
                select c
                from Partners p
                join Company c on c.id = p.customerId
                where p.supplierId = :supplierId
                  and c.phone = :phone
                  and c.telegramUserId is not null
                  and c.deleted = false
                  and p.deleted = false
            """)
    Optional<Company> findCustomerBySupplierIdAndPhone(@Param("supplierId") Long supplierId, @Param("phone") String phone);

    Optional<Company> findByIdAndDeletedIsFalseAndStatus(@Param("id") Long id, CompanyStatus status);
}
