package uz.uzkassa.cts.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.hrms.Vacancy;
import uz.uzkassa.cts.repository.custom.VacancyRepositoryCustom;

@Repository
public interface VacancyRepository extends BaseRepository<Vacancy, Long>, VacancyRepositoryCustom {

    @Query("SELECT coalesce(max(v.incrementNumber), 0) FROM Vacancy v WHERE v.companyId = :companyId")
    Long getMaxIncrementNumber(@Param("companyId") Long companyId);
}