package uz.uzkassa.cts.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.CustomerFiscalModule;
import uz.uzkassa.cts.repository.custom.CustomerFiscalModuleRepositoryCustom;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerFiscalModuleRepository extends BaseRepository<CustomerFiscalModule, Long>, CustomerFiscalModuleRepositoryCustom {

    CustomerFiscalModule findByCustomerIdAndFiscalModuleId(Long customerId, Long fiscalModuleId);

    CustomerFiscalModule findByFiscalModuleId(Long fiscalModuleId);

    @Query(value = "select fm.serial_number from customer_fiscal_module cfm inner join fiscal_module fm on cfm.fiscal_module_id =fm.id where cfm.customer_id = ?1", nativeQuery = true)
    List<String> findByCustomerIdWithDeleted(Long customerId);

    @Query(value = "select * from customer_fiscal_module cfm where cfm.id=?1", nativeQuery = true)
    CustomerFiscalModule findByIdWithDeleted(Long id);

    @Query("select count(cfm.id) from CustomerFiscalModule cfm where cfm.deleted=false and cfm.fiscalModuleId= ?1")
    Long totalByFiscalModuleId(Long fiscalModuleId);

    Optional<CustomerFiscalModule> findFirstByFiscalModuleIdOrderByIdDesc(Long fiscalModuleId);
}
