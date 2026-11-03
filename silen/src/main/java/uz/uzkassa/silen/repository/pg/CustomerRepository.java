package uz.uzkassa.silen.repository.pg;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.silen.domain.Customer;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data repository for the Customer entity.
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>, CustomerRepositoryCustom {

    Optional<Customer> findFirstByOrganizationIdAndTinAndDeletedIsFalse(String organizationId, String customerTin);

    Optional<Customer> findFirstByIdNotAndOrganizationIdAndTinAndDeletedIsFalse(Long id, String organizationId, String customerTin);

    List<Customer> findAllByDeletedIsFalse();

    @Query("select id from Customer where organizationId=?1")
    List<Long> findAllIds(String organizationId);

    @Query("UPDATE Customer SET hasFinancialDiscount=?2 WHERE id=?1")
    @Modifying
    void updateFinancialDiscount(Long customerId, boolean discountAllProducts);

    @Query("select c.tin from Customer c where c.organizationId=:organizationId and c.isCommittent is true")
    List<String> findAllCommittentsTinByOrganizationIdAndComissionIsTrue(@Param("organizationId") String organizationId);


    @Query("select c.tin from Customer c where c.organization.tin=:tin and c.isCommittent is true")
    List<String> findAllCommittentsTinByOrganizationTinAndComissionIsTrue(@Param("tin") String tin);


    boolean existsCustomerByOrganizationIdAndTinAndDeletedIsFalse(String organizationId, String tin);

}
