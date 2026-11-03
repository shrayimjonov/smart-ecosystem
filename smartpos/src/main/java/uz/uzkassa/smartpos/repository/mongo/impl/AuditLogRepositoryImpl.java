package uz.uzkassa.smartpos.repository.mongo.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpos.domain.mongo.AuditLog;
import uz.uzkassa.smartpos.enums.SortTypeEnum;
import uz.uzkassa.smartpos.filter.AuditFilter;
import uz.uzkassa.smartpos.repository.mongo.AuditLogRepositoryCustom;

@Repository
public class AuditLogRepositoryImpl implements AuditLogRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    public AuditLogRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Page<AuditLog> getList(AuditFilter filter) {
        Query query = new Query();
        Criteria baseCriteria = new Criteria();
        if (filter.getEntityType() != null) {
            baseCriteria = baseCriteria.and("entityType").is(filter.getEntityType());
        }
        if (filter.getEntityId() != null) {
            baseCriteria = baseCriteria.and("entityId").is(filter.getEntityId());
        }
        if (filter.getField() != null) {
            baseCriteria = baseCriteria.and("field").is(filter.getField());
        }
        if (filter.getCompanyId() != null) {
            baseCriteria = baseCriteria.and("companyId").is(filter.getCompanyId());
        }
        if (StringUtils.isNotBlank(filter.getUpdater())) {
            baseCriteria = baseCriteria.and("updater").is(filter.getUpdater());
        }
        if (filter.getFrom() != null) {
            baseCriteria = baseCriteria.and("modificationDate").gte(filter.getFrom());
        }
        if (filter.getTo() != null) {
            baseCriteria = baseCriteria.and("modificationDate").lte(filter.getTo());
        }
        query.addCriteria(baseCriteria);
        if (StringUtils.isNotEmpty(filter.getSearch())) {
            Criteria searchCriteria = new Criteria();
            searchCriteria.orOperator(
                    Criteria.where("field").regex(filter.getSearch(), "i"),
                    Criteria.where("fromStringValue").regex(filter.getSearch(), "i"),
                    Criteria.where("toStringValue").regex(filter.getSearch(), "i"),
                    Criteria.where("updater").regex(filter.getSearch(), "i")
            );
            query.addCriteria(searchCriteria);
        }
        Sort sorting;
        if (StringUtils.isNotEmpty(filter.getOrderBy())) {
            Sort.Direction direction = SortTypeEnum.asc.equals(filter.getSortOrder()) ? Sort.Direction.ASC : Sort.Direction.DESC;
            sorting = switch (filter.getOrderBy()) {
                case "field" -> Sort.by(direction, "field");
                case "entityType" -> Sort.by(direction, "entityType");
                default -> Sort.by(Sort.Direction.DESC, "id");
            };
        } else {
            sorting = Sort.by(Sort.Direction.DESC, "id");
        }
        query.with(sorting);
        query.with(filter.getPageable());

        return PageableExecutionUtils.getPage(
                mongoTemplate.find(query, AuditLog.class),
                filter.getPageable(),
                () -> mongoTemplate.count(Query.of(query).limit(-1).skip(-1), AuditLog.class));
    }
}
