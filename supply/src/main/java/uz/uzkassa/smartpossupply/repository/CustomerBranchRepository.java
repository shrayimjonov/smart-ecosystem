package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.customerbranch.CustomerBranch;
import uz.uzkassa.smartpossupply.enums.CommonStatus;

import java.util.Optional;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 19.01.2023 13:41
 */
@Repository
public interface CustomerBranchRepository extends BaseRepository<CustomerBranch, Long>, CustomerBranchRepositoryCustom {

    boolean existsByCustomerIdAndDeletedIsFalseAndNameIgnoreCase(Long customerId, String name);

    boolean existsByCustomerIdAndDeletedIsFalseAndNameIgnoreCaseAndIdNot(Long customerId, String name, Long id);

    boolean existsByCustomerIdAndCustomerBranchNumberAndIdNot(Long customerId, String customerBranchNumber, Long id);

    boolean existsByCustomerIdAndCustomerBranchNumber(Long customerId, String customerBranchNumber);

    boolean existsByCustomerIdAndMarketCode(Long customerId, String marketCode);

    boolean existsByCustomerIdAndMarketCodeAndIdNot(Long customerId, String marketCode, Long id);

    boolean existsByCustomerId(Long customerId);

    boolean existsByCustomerIdAndMainIsTrue(Long customerId);

    Optional<CustomerBranch> findFirstByCustomerIdAndCustomerBranchNumber(Long customerId, String customerBranchNumber);

    Optional<CustomerBranch> findFirstByCustomerIdAndStatusOrderById(Long customerId, CommonStatus status);

    Optional<CustomerBranch> findFirstByCustomerIdAndName(Long customerId, String name);

    Optional<CustomerBranch> findFirstByCustomerIdAndMarketCode(Long customerId, String marketCode);

    Optional<CustomerBranch> findFirstByCustomerIdAndNameIgnoreCase(Long customerId, String name);

    Integer countByCustomerIdAndDeletedIsFalseAndStatus(Long customerId, CommonStatus status);

    @Modifying
    @Query("update CustomerBranch set main=false where customerId=:customerId and id<>:id")
    void updateSetMainFalseByCompanyId(@Param("customerId") Long customerId, @Param("id") Long id);

    @Query("select cb from CustomerBranch cb where cb.deleted = false and cb.main = true and cb.customerId=:customerId")
    CustomerBranch getMain(@Param("customerId") Long customerId);

    Optional<CustomerBranch> findByMobileId(String mobileId);

    boolean existsByIdAndCustomerIdAndDeletedIsFalse(Long id, Long customerId);

    Optional<CustomerBranch> findFirstByExternalIdAndDeletedIsFalse(String externalId);
}
