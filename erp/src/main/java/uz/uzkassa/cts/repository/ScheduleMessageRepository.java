package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.ScheduleMessage;
import uz.uzkassa.cts.enums.ScheduleMessageStatus;
import uz.uzkassa.cts.repository.custom.ScheduleMessageRepositoryCustom;

import java.util.Optional;

@Repository
public interface ScheduleMessageRepository extends BaseRepository<ScheduleMessage, Long>, ScheduleMessageRepositoryCustom {

    Optional<ScheduleMessage> findByIdAndStatus(Long id, ScheduleMessageStatus status);
}
