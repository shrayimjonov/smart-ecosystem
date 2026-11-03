package uz.uzkassa.horeca.repository;

import uz.uzkassa.horeca.domain.CompanyCustomer;
import uz.uzkassa.horeca.domain.company.Company;
import uz.uzkassa.horeca.enums.CommonStatus;

import java.util.List;
import java.util.Optional;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 27.09.2024
 */

public interface CompanyCustomerRepository extends BaseRepository<CompanyCustomer, Long> {

    boolean existsByCompanyIdAndCustomerIdAndBranchId(Long companyId, Long customerId, Long branchId);
}
