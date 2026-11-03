package uz.uzkassa.cts.service;

import uz.uzkassa.cts.domain.Relation;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.enums.RelationType;

import java.util.Optional;

public interface RelationService extends BaseService<Relation, Long> {

    void create(Long fromId, RelationType fromType, String toId, RelationType toType, EntityTypeEnum entityType);

    Relation get(String toId, RelationType toType, EntityTypeEnum entityType);
}
