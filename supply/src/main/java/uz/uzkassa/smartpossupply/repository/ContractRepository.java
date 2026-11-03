package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.factura.Contract;
import uz.uzkassa.smartpossupply.enums.ContractStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContractRepository extends BaseRepository<Contract, Long>, ContractRepositoryCustom {

    Optional<Contract> findFirstBySupplierIdAndCustomerIdAndContractNumberAndContractDateAndDeletedIsFalse(Long supplierId, Long customerId, String contractNumber, LocalDate contractDate);

    boolean existsBySupplierIdAndCustomerIdAndContractNumberAndContractDateAndDeletedIsFalse(Long supplierId, Long customerId, String contractNumber, LocalDate contractDate);

    boolean existsBySupplierIdAndCustomerIdAndContractNumberAndContractDateAndDeletedIsFalseAndIdNot(Long supplierId, Long customerId, String contractNumber, LocalDate contractDate, Long id);

    List<Contract> findAllByCompanyIdAndStatus(Long companyId, ContractStatus status);

    List<Contract> findAllByCustomerIdAndStatus(Long customerId, ContractStatus status);

    Optional<Contract> findByMobileId(String mobileId);
}
