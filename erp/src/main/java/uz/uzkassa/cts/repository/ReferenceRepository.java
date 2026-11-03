package uz.uzkassa.cts.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.constants.CacheConstants;
import uz.uzkassa.cts.domain.Reference;
import uz.uzkassa.cts.enums.ReferenceType;
import uz.uzkassa.cts.repository.custom.ReferenceRepositoryCustom;

import java.util.List;

@Repository
public interface ReferenceRepository extends BaseRepository<Reference, Long>, ReferenceRepositoryCustom {

    @Cacheable(cacheNames = CacheConstants.REFERENCE_ENTITY_BY_TYPE_CACHE)
    @Query("select id from Reference where deleted=false and type=:type")
    List<Long> findAllIdsByTypeAndDeletedIsFalse(@Param("type") ReferenceType type);

    @Cacheable(cacheNames = CacheConstants.REFERENCE_ENTITY_BY_TYPE_AND_CODE_CACHE)
    Reference findFirstByTypeAndCodeAndDeletedIsFalse(ReferenceType type, String code);
}
