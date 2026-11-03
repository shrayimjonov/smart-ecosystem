package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.Tariff;
import uz.uzkassa.cts.repository.custom.TariffRepositoryCustom;

@Repository
public interface TariffRepository extends BaseRepository<Tariff, Long>, TariffRepositoryCustom {
}
