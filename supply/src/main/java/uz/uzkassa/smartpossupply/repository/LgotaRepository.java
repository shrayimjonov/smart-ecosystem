package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.Lgota;

import java.util.Optional;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 21.07.2023
 */
@Repository
public interface LgotaRepository extends BaseRepository<Lgota, Long>, LgotaRepositoryCustom {

    boolean existsByCatalogCode(String catalogCode);

    Optional<Lgota> findFirstByCatalogCode(String catalogCode);
}
