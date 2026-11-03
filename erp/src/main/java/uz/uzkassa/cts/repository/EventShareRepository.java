package uz.uzkassa.cts.repository;

import feign.Param;
import org.springframework.data.jpa.repository.Query;
import uz.uzkassa.cts.domain.hrms.EventShare;

import java.util.List;

public interface EventShareRepository extends BaseRepository<EventShare, Long> {

    void deleteAllByEventId(Long eventId);

    @Query("select s.participantId from EventShare s where s.eventId=:eventId")
    List<Long> getParticipantIds(@Param("eventId") Long eventId);
}
