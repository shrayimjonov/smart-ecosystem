package uz.uzkassa.cts.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.uzkassa.cts.domain.hrms.BenefitRequest;
import uz.uzkassa.cts.repository.custom.BenefitRequestRepositoryCustom;

public interface BenefitRequestRepository extends BaseRepository<BenefitRequest, Long>, BenefitRequestRepositoryCustom {

    @Query("SELECT COALESCE(MAX(br.incrementNumber), 0) FROM BenefitRequest br WHERE br.companyId = :companyId")
    Long getMaxIncrementNumber(@Param("companyId") Long companyId);
}
