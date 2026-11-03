package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.Relation;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.enums.RelationType;

import java.util.Optional;

@Repository
public interface RelationRepository extends BaseRepository<Relation, Long> {

    Optional<Relation> findFirstByFromIdAndFromTypeAndEntityType(Long fromId, RelationType fromType, EntityTypeEnum entityType);

    Optional<Relation> findFirstByToIdAndToTypeAndEntityType(String toId, RelationType toType, EntityTypeEnum entityType);
}
