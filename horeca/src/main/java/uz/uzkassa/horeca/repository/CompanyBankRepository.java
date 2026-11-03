package uz.uzkassa.horeca.repository;

import uz.uzkassa.horeca.domain.company.CompanyBank;

import java.util.Optional;

public interface CompanyBankRepository extends BaseRepository<CompanyBank, Long> {
    Optional<CompanyBank> findFirstByCompanyIdAndBankIdAndDeletedIsFalse(Long companyId, Long bankId);

    Optional<CompanyBank> findFirstByCompanyIdAndDeletedIsFalse(Long companyId);
}
