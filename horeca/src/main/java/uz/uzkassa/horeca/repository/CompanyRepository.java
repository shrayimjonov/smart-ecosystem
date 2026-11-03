package uz.uzkassa.horeca.repository;

import uz.uzkassa.horeca.domain.company.Company;
import uz.uzkassa.horeca.enums.CommonStatus;

import java.util.List;
import java.util.Optional;


public interface CompanyRepository extends BaseRepository<Company, Long>, CompanyRepositoryCustom {
    Optional<Company> findByTin(String tin);

    Optional<Company> findByTinAndDeletedIsFalse(String tin);

    List<Company> findAllByStatusAndOwnerIdIsNotNullAndDeletedIsFalse(CommonStatus status);

    Optional<Company> findFirstByTin(String companyInn);
}
