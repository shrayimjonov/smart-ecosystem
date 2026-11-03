package uz.uzkassa.silen.repository.mongo.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.Meta;
import org.springframework.stereotype.Repository;
import uz.uzkassa.silen.domain.mongo.Mark;
import uz.uzkassa.silen.dto.filter.MongoFilter;
import uz.uzkassa.silen.enumeration.SortTypeEnum;
import uz.uzkassa.silen.repository.mongo.MarkRepositoryCustom;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;

@Repository
public class MarkRepositoryImpl implements MarkRepositoryCustom<Mark> {

    private final MongoTemplate mongoTemplate;

    public MarkRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    @Meta(allowDiskUse = true)
    public Page<Mark> findAllByFilter(MongoFilter filter) {
        Query query = new Query();
        if (filter.getPartyId() != null) {
            query.addCriteria(Criteria.where("partyId").is(filter.getPartyId()));
        }
        if (filter.getOrderProductId() != null) {
            query.addCriteria(Criteria.where("orderProductId").is(filter.getOrderProductId()));
        }
        if (filter.getProductId() != null) {
            query.addCriteria(Criteria.where("productId").is(filter.getProductId()));
        }
//        if (filter.getOrganizationId() != null) {
//            query.addCriteria(Criteria.where("organizationId").is(filter.getOrganizationId()));
//        }
        if (filter.getUsed() != null) {
            query.addCriteria(Criteria.where("used").is(filter.getUsed()));
        }
        if (filter.getAggregationStatus() != null) {
            query.addCriteria(Criteria.where("status").is(filter.getAggregationStatus()));
        }
        if (filter.checkSearchIsEmpty()) {
            Criteria searchCriteria = new Criteria();
            searchCriteria.orOperator(
                    Criteria.where("printCode").regex(filter.getSearch(), "i"),
                    Criteria.where("code").regex(filter.getSearch(), "i"),
                    Criteria.where("parentCode").regex(filter.getSearch(), "i")
            );
            query.addCriteria(searchCriteria);
        }
        final long totalCount = mongoTemplate.count(query, Mark.class);

        Sort.Direction direction = SortTypeEnum.asc.equals(filter.getSortOrder()) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sorting = Sort.by(direction, Objects.toString(filter.getOrderBy(), "lastModifiedDate"));
        query.with(sorting);
        query.with(filter.getPageable());

        return new PageImpl<>(mongoTemplate.find(query, Mark.class), filter.getPageable(), totalCount);
    }


    @Override
    public BigDecimal findAllByFilterStats(MongoFilter filter) {
        List<AggregationOperation> matches = new ArrayList<>();
        if (filter.getPartyId() != null) {
            matches.add(new MatchOperation(Criteria.where("partyId").is(filter.getPartyId())));
        }
        if (filter.getOrderProductId() != null) {
            matches.add(new MatchOperation(Criteria.where("orderProductId").is(filter.getOrganizationId())));
        }
        if (filter.getProductId() != null) {
            matches.add(new MatchOperation(Criteria.where("productId").is(filter.getProductId())));
        }
        if (filter.getOrganizationId() != null) {
            matches.add(new MatchOperation(Criteria.where("organizationId").is(filter.getOrganizationId())));
        }
        if (filter.getUsed() != null) {
            matches.add(new MatchOperation(Criteria.where("used").is(filter.getUsed())));
        }
        if (filter.getFrom() != null) {
            matches.add(new MatchOperation(Criteria.where("createdDate").gte(filter.getUsed())));
        }
        if (filter.getTo() != null) {
            matches.add(new MatchOperation(Criteria.where("createdDate").lte(filter.getUsed())));
        }
        if (filter.checkSearchIsEmpty()) {
            Criteria searchCriteria = new Criteria();
            searchCriteria.orOperator(Criteria.where("printCode").regex(filter.getSearch(), "i"), Criteria.where("code").regex(filter.getSearch(), "i"));
            matches.add(new MatchOperation(searchCriteria));
        }

        GroupOperation groupOperation = group("organizationId").sum("quantity").as("quantity");
        matches.add(groupOperation);

        Aggregation aggregate = Aggregation.newAggregation(matches);

        AggregationResults<Mark> orderAggregate = mongoTemplate.aggregate(aggregate, Mark.class, Mark.class);
        return Optional.of(orderAggregate.getMappedResults())
                .filter(CollectionUtils::isNotEmpty)
                .map(List::getFirst)
                .map(Mark::getQuantity)
                .map(BigDecimal::valueOf)
                .orElse(BigDecimal.ZERO);
    }

    @Override
    public List<Mark> getPartyAggregations(String partyAggregationId) {
        return List.of();
    }

}
