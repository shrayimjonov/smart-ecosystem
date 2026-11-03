package uz.uzkassa.cts.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.RepairTicket;
import uz.uzkassa.cts.repository.custom.RepairTicketRepositoryCustom;

@Repository
public interface RepairTicketRepository extends BaseRepository<RepairTicket, Long>, RepairTicketRepositoryCustom {

    boolean existsByDeviceId(Long deviceId);

    @Query("SELECT coalesce(max(rt.incrementNumber), 0) FROM RepairTicket rt where rt.companyId=:companyId")
    Long getMaxIncrementNumber(@Param("companyId") Long companyId);
}
