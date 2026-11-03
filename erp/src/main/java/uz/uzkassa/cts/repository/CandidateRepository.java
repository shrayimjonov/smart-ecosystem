package uz.uzkassa.cts.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.hrms.Candidate;
import uz.uzkassa.cts.repository.custom.CandidateRepositoryCustom;

@Repository
public interface CandidateRepository extends BaseRepository<Candidate, Long>, CandidateRepositoryCustom {

    @Query("SELECT coalesce(max(c.incrementNumber), 0) FROM Candidate c WHERE c.companyId = :companyId")
    Long getMaxIncrementNumber(@Param("companyId") Long companyId);
}