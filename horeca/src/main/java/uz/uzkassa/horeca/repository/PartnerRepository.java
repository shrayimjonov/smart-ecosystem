package uz.uzkassa.horeca.repository;

import org.springframework.data.jpa.repository.Modifying;
import uz.uzkassa.horeca.domain.company.Partner;

public interface PartnerRepository extends BaseRepository<Partner, Long>, PartnerRepositoryCustom {
    @Modifying
    void deleteBySupplierIdAndCustomerId(Long supplierId, Long customerId);

    boolean existsBySupplierIdAndCustomerIdAndCompanyId(Long supplierId, Long customerId, Long companyId);
}
