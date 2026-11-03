package uz.uzkassa.cts.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.constants.CacheConstants;
import uz.uzkassa.cts.domain.Integration;
import uz.uzkassa.cts.enums.IntegrationType;
import uz.uzkassa.cts.repository.custom.IntegrationRepositoryCustom;

import java.util.Optional;

@Repository
public interface IntegrationRepository extends BaseRepository<Integration, Long>, IntegrationRepositoryCustom {

    @Cacheable(cacheNames = CacheConstants.INTEGRATION_BY_TYPE_CACHE)
    Optional<Integration> findFirstByTypeAndDeletedIsFalse(IntegrationType type);

    boolean existsByTypeAndDeletedIsFalse(IntegrationType type);

    boolean existsByTypeAndDeletedIsFalseAndIdNot(IntegrationType type, Long id);
}
