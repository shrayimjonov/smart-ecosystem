package uz.uzkassa.cts.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.hrms.VacancyApplication;
import uz.uzkassa.cts.repository.custom.VacancyApplicationRepositoryCustom;

@Repository
public interface VacancyApplicationRepository extends BaseRepository<VacancyApplication, Long>, VacancyApplicationRepositoryCustom {

    @Query("SELECT coalesce(max(va.incrementNumber), 0) FROM VacancyApplication va WHERE va.companyId = :companyId")
    Long getMaxIncrementNumber(@Param("companyId") Long companyId);
}
