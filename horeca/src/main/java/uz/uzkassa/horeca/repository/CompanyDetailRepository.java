package uz.uzkassa.horeca.repository;

import uz.uzkassa.horeca.domain.company.CompanyDetail;

import java.util.Optional;


public interface CompanyDetailRepository extends BaseRepository<CompanyDetail, Long> {
    Optional<CompanyDetail> findFirstByCompanyId(Long companyId);
}
