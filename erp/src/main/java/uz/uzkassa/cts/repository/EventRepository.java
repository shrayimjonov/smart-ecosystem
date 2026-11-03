package uz.uzkassa.cts.repository;

import uz.uzkassa.cts.domain.hrms.Event;
import uz.uzkassa.cts.repository.custom.EventRepositoryCustom;

public interface EventRepository extends BaseRepository<Event, Long>, EventRepositoryCustom {
}
