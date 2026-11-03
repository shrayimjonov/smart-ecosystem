package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.SmsHistory;

@Repository
public interface SmsHistoryRepository extends BaseRepository<SmsHistory, Long> {
}
