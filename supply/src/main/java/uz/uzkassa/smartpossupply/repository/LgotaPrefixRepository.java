package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.LgotaPrefix;

import java.util.List;
import java.util.Optional;

@Repository
public interface LgotaPrefixRepository extends BaseRepository<LgotaPrefix, Long> {

    Optional<LgotaPrefix> findDistinctTopByPrefixIgnoreCaseAndNameIgnoreCase(String prefix, String name);

    List<LgotaPrefix> findAllByOrderByPrefixAsc();
}
