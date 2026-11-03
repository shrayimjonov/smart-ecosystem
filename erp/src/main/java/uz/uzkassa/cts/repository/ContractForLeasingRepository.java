package uz.uzkassa.cts.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.ContractForLeasing;
import uz.uzkassa.cts.enums.CompanyType;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.enums.DeviceType;

import java.util.List;

@Repository
public interface ContractForLeasingRepository extends BaseRepository<ContractForLeasing, Long> {
    ContractForLeasing findByStatusAndIdAndCompanyTypeOrderByIdDesc(ContractStatus status, Long id, CompanyType companyType);

    List<ContractForLeasing> findByStatusAndCustomerIdAndCompanyTypeAndDeviceTypeOrderByIdDesc(ContractStatus status, Long customerId, CompanyType companyType, DeviceType deviceType);

    @EntityGraph(attributePaths = {"customer", "creator", "modelPriceHistory", "creator"})
    Page<ContractForLeasing> findAll(Specification<ContractForLeasing> specification, Pageable pageable);
}
