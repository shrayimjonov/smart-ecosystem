package uz.uzkassa.cts.repository;

import uz.uzkassa.cts.domain.PanBlackList;
import uz.uzkassa.cts.repository.BaseRepository;

import java.util.Optional;

public interface PanBlackListRepository extends BaseRepository<PanBlackList, Long> {
    Optional<PanBlackList> findByPan(String pan);
}
