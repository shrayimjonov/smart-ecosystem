package uz.uzkassa.cts.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.Relation;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.enums.RelationType;
import uz.uzkassa.cts.repository.RelationRepository;
import uz.uzkassa.cts.service.RelationService;

@Transactional(readOnly = true)
@Service
public class RelationServiceImpl extends BaseServiceImpl<Relation, Long> implements RelationService {

    private final RelationRepository relationRepository;

    public RelationServiceImpl(RelationRepository relationRepository) {
        super(relationRepository);
        this.relationRepository = relationRepository;
    }

    @Override
    @Transactional
    public void create(Long fromId, RelationType fromType, String toId, RelationType toType, EntityTypeEnum entityType) {
        Relation relation = relationRepository.findFirstByFromIdAndFromTypeAndEntityType(fromId, fromType, entityType).orElse(new Relation());
        relation.setFromId(fromId);
        relation.setFromType(fromType);
        relation.setToId(toId);
        relation.setToType(toType);
        relation.setEntityType(entityType);
        relationRepository.save(relation);
    }

    @Override
    public Relation get(String toId, RelationType toType, EntityTypeEnum entityType) {
        return relationRepository.findFirstByToIdAndToTypeAndEntityType(toId, toType, entityType).orElse(null);
    }
}
