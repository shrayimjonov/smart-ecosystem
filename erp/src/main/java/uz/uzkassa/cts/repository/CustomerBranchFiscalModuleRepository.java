package uz.uzkassa.cts.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.CustomerBranchFiscalModule;
import uz.uzkassa.cts.repository.custom.CustomerBranchFiscalModuleRepositoryCustom;

import java.util.Optional;

@Repository
public interface CustomerBranchFiscalModuleRepository extends BaseRepository<CustomerBranchFiscalModule, Long>, CustomerBranchFiscalModuleRepositoryCustom {

    @Query("select count(cbfm.id) from CustomerBranchFiscalModule cbfm where cbfm.deleted=false and cbfm.customerBranchId= ?1")
    Long totalByCustomerBranchId(Long customerBranchId);

    CustomerBranchFiscalModule findFirstByFiscalModuleId(Long fiscalModuleId);

    CustomerBranchFiscalModule findFirstByCustomerBranchIdAndFiscalModuleId(Long customerBranchId, Long fiscalModuleId);

    CustomerBranchFiscalModule findFirstByCustomerBranch_CustomerIdAndFiscalModuleId(Long customerId, Long fiscalModuleId);

    @Query(value = "select * from customer_branch_fiscal_module cbfm where cbfm.id=?1", nativeQuery = true)
    CustomerBranchFiscalModule findByIdWithDeleted(Long id);

    Optional<CustomerBranchFiscalModule> findFirstByFiscalModuleIdOrderByIdDesc(Long fiscalModuleId);

    @Query("select count(cbfm.id) from CustomerBranchFiscalModule cbfm " +
            "join CustomerBranch cb on cbfm.customerBranchId = cb.id " +
            "where cb.customerId = :customerId")
    Long countAllByCustomerId(@Param("customerId") Long customerId);

    Long countAllByCustomerBranchId(Long customerBranchId);

}
