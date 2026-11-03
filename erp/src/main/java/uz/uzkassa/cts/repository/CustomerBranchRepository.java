package uz.uzkassa.cts.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.CustomerBranch;
import uz.uzkassa.cts.repository.custom.CustomerBranchRepositoryCustom;

import java.util.List;

@Repository
public interface CustomerBranchRepository extends BaseRepository<CustomerBranch, Long>, CustomerBranchRepositoryCustom {

    boolean existsByNameIsIgnoreCaseAndCustomerIdAndDeletedIsFalse(String name, Long customerId);

    boolean existsByNameIsIgnoreCaseAndCustomerIdAndDeletedIsFalseAndIdNot(String name, Long customerId, Long id);

    Long countAllByCustomerIdAndDeletedIsFalse(Long customerId);

    CustomerBranch findFirstByOfdId(Long ofdId);

    @Query(value = "select cb.* from customer_branch cb where cb.deleted=false and cb.customer_id=?1 and cb.id not in(select cbd.customer_branch_id from customer_branch_device cbd) ", nativeQuery = true)
    List<CustomerBranch> findEmptyList(Long customerId);

    @Query(value = "select id from customer_branch", nativeQuery = true)
    List<Long> findAllBranchIds();

    @Query(value = """
            SELECT count(cb.id) FROM customer_branch cb
            INNER JOIN customer c on c.id = cb.customer_id
            WHERE c.company_id=?1""", nativeQuery = true)
    Long countByCompanyId(Long companyId);

    Long countAllByBranchIdAndDeletedIsFalse(Long branchId);
}
