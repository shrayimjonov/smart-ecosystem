package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.company.CompanyFieldHistory;
import uz.uzkassa.smartpossupply.enums.FieldHistoryType;

@Repository
public interface CompanyFieldHistoryRepository extends BaseRepository<CompanyFieldHistory, Long>, CompanyFieldHistoryRepositoryCustom {
    boolean existsByCompanyIdAndFieldTypeAndIdentifierAndDeletedFalse(Long companyId, FieldHistoryType fieldType, String identifier);
}