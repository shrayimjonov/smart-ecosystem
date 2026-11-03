package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.OfdSyncHistory;

@Repository
public interface OfdSyncHistoryRepository extends BaseRepository<OfdSyncHistory, Long> {
}
