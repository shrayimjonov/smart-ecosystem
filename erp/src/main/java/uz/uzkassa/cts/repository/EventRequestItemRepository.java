package uz.uzkassa.cts.repository;

import uz.uzkassa.cts.domain.hrms.EventRequestItem;

public interface EventRequestItemRepository extends BaseRepository<EventRequestItem, Long> {

    void deleteAllByEventRequestId(Long eventRequestId);
}
