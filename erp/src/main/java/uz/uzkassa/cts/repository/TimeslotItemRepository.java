package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.timeslot.TimeslotItem;

@Repository
public interface TimeslotItemRepository extends BaseRepository<TimeslotItem, Long> {
}
