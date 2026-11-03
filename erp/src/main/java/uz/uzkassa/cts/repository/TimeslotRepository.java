package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.timeslot.Timeslot;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.repository.custom.TimeslotRepositoryCustom;

import java.util.List;
import java.util.Optional;

@Repository
public interface TimeslotRepository extends BaseRepository<Timeslot, Long>, TimeslotRepositoryCustom {

    Optional<Timeslot> findFirstByRoundTheClockIsTrueAndDeletedIsFalse();

    Optional<Timeslot> findFirstByEntityIdAndEntityType(Long entityId, EntityTypeEnum entityType);

    List<Timeslot> findAllByEntityIdIsNullAndEntityTypeIsNullAndDeletedIsFalse();
}
