package uz.uzkassa.cts.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.uzkassa.cts.domain.hrms.EventRequest;
import uz.uzkassa.cts.repository.custom.EventRequestRepositoryCustom;

public interface EventRequestRepository extends BaseRepository<EventRequest, Long>, EventRequestRepositoryCustom {

    @Query("SELECT COALESCE(MAX(er.incrementNumber), 0) FROM EventRequest er WHERE er.companyId = :companyId")
    Long getMaxIncrementNumber(@Param("companyId") Long companyId);
}
