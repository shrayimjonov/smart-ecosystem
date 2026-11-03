package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.mailing.ScheduleMessage;
import uz.uzkassa.smartpossupply.enums.ScheduleMessageStatus;

import java.util.Optional;

@Repository
public interface ScheduleMessageRepository extends BaseRepository<ScheduleMessage, Long>, ScheduleMessageRepositoryCustom {

    @EntityGraph(attributePaths = "mailingList")
    Optional<ScheduleMessage> findByIdAndStatus(Long id, ScheduleMessageStatus status);

}
