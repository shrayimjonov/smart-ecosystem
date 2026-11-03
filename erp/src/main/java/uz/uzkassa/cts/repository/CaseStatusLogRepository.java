package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.CaseStatusLog;
import uz.uzkassa.cts.repository.custom.CaseStatusLogRepositoryCustom;

@Repository
public interface CaseStatusLogRepository extends BaseRepository<CaseStatusLog, Long>, CaseStatusLogRepositoryCustom {

}
