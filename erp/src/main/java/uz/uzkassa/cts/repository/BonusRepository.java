package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.Bonus;
import uz.uzkassa.cts.repository.custom.BonusRepositoryCustom;

@Repository
public interface BonusRepository extends BaseRepository<Bonus, Long>, BonusRepositoryCustom {
}
