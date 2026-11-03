package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.ActOfTransmission;
import uz.uzkassa.cts.enums.CompanyType;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.repository.custom.ActOfTransmissionRepositoryCustom;

import java.util.List;

@Repository
public interface ActOfTransmissionRepository extends BaseRepository<ActOfTransmission, Long>, ActOfTransmissionRepositoryCustom {
    List<ActOfTransmission> findByContract_IdOrderByIdDesc(Long contractId);

    List<ActOfTransmission> findByStatusAndCompanyTypeAndInvoiceIsNull(ContractStatus status, CompanyType type);
}
