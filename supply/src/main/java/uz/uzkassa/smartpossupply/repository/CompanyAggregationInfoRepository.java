package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.company.CompanyAggregationInfo;
import uz.uzkassa.smartpossupply.enums.CompanyStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyAggregationInfoRepository extends BaseRepository<CompanyAggregationInfo, Long>, CompanyAggregationInfoRepositoryCustom {
    Optional<CompanyAggregationInfo> findByCompanyId(Long companyId);

    @EntityGraph(attributePaths = "company")
    List<CompanyAggregationInfo> findAllByAslBelgiTokenIsNotNullAndAslBelgiTokenDateIsAfter(LocalDateTime minusHours);

    Optional<CompanyAggregationInfo> findAllByCompanyTinAndAslBelgiTokenIsNotNullAndAslBelgiTokenDateIsAfter(String companyTin, LocalDateTime minusHours);

    List<CompanyAggregationInfo> findAllByAslBelgiTokenIsNotNullAndDeletedIsFalseAndCompanyStatusAndCompanyDeletedIsFalse(CompanyStatus companyStatus);

    List<CompanyAggregationInfo> findAllByAslBelgiXtraceTokenExpireDateBefore(LocalDateTime date);

}
