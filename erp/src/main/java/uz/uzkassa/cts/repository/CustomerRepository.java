package uz.uzkassa.cts.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.Customer;
import uz.uzkassa.cts.repository.custom.CustomerRepositoryCustom;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends BaseRepository<Customer, Long>, CustomerRepositoryCustom {

    Customer findByTin(String tin);

    Optional<Customer> findFirstByTin(String tin);

    @Query(value = "SELECT c.id FROM Customer c WHERE c.tin =:tin")
    Long getIdByTin(@Param("tin") String tin);

    boolean existsByTin(String tin);

    boolean existsByTinAndIdNot(String tin, Long id);

    Customer findFirstByTinOrOldTin(String tin, String oldTin);

    List<Customer> findAllByTinInOrOldTinIn(Collection<String> tins, Collection<String> oldTins);

    <T> Optional<T> findFirstByTinOrOldTin(String tin, String oldTin, Class<T> type);

    Optional<Customer> findByPhone(String phone);

    boolean existsByPhone(String phone);

    boolean existsByPhoneAndIdNot(String phone, Long id);

    Long countByCompanyId(Long companyId);

    @Query("select c from Customer c join c.branches b where c.deleted = false and c.id = :id and b.id=:branchId")
    Customer getByIdAndBranchId(@Param("id") Long id,
                                @Param("branchId") Long branchId);

    @Query(value = "select c.tin from customer c where c.company_id = :companyId and c.deleted is false and c.tin is not null order by c.id\n", nativeQuery = true)
    List<String> getTinsByCompanyId(@Param("companyId") Long companyId);

    @Query(value = "select c.tin from customer c where c.deleted is false and length(c.tin) = 14 and (c.old_tin is null or length(c.old_tin) = 14) order by c.id\n", nativeQuery = true)
    List<String> getTinsOldTinNull();

    @Modifying
    @Query("update Customer c set c.managerId =:newManagerId where c.managerId =:oldManagerId")
    void changeManager(@Param("oldManagerId") Long oldManagerId,
                       @Param("newManagerId") Long newManagerId);

    @Query(value = "select cb.customer_id from customer_branches cb where cb.branch_id = :branchId", nativeQuery = true)
    List<Long> getIdsByBranchId(@Param("branchId") Long branchId);

    Long countAllByBranchIdAndDeletedIsFalse(Long branchId);
}
