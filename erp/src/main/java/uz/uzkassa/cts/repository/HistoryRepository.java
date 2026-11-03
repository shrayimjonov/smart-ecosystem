package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.History;
import uz.uzkassa.cts.repository.custom.HistoryRepositoryCustom;

@Repository
public interface HistoryRepository extends BaseRepository<History, Long>, HistoryRepositoryCustom {
}
