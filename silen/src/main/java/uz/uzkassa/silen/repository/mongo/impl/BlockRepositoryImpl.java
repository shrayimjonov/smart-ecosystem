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
import uz.uzkassa.silen.domain.mongo.Block;
import uz.uzkassa.silen.dto.filter.MongoFilter;
import uz.uzkassa.silen.enumeration.PackageType;
import uz.uzkassa.silen.enumeration.SortTypeEnum;
import uz.uzkassa.silen.repository.mongo.MarkRepositoryCustom;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;

/**
 * Pattern Matching Using $regex Operator options
 * i: To match both lower case and upper case pattern in the string.
 * m: To include ^ and $ in the pattern in the match i.e. to specifically search for ^ and $ inside the string. Without this option, these anchors match at the beginning or end of the string.
 * x: To ignore all white space characters in the $regex pattern.
 * s: To allow the dot character “.” to match all characters including newline characters.
 */
@Repository
public class BlockRepositoryImpl implements MarkRepositoryCustom<Block> {

    private final MongoTemplate mongoTemplate;

    public BlockRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    @Meta(allowDiskUse = true)
    public Page<Block> findAllByFilter(MongoFilter filter) {
        Query query = new Query();
        if (filter.getPartyId() != null) {
            query.addCriteria(Criteria.where("partyId").is(filter.getPartyId()));
        }
        if (filter.getUnit() != null) {
            query.addCriteria(Criteria.where("unit").is(filter.getUnit()));
        }
        if (filter.getPartitionId() != null) {
            query.addCriteria(Criteria.where("partitionId").is(filter.getPartitionId()));
        }
        if (filter.getType() != null) {
            query.addCriteria(Criteria.where("aggregationType").is(filter.getType()));
        }
        if (filter.getOrderProductId() != null) {
            query.addCriteria(Criteria.where("orderProductId").is(filter.getOrderProductId()));
        }
        if (filter.getProductId() != null) {
            query.addCriteria(Criteria.where("productId").is(filter.getProductId()));
        }
        if (filter.getOrganizationId() != null) {
            query.addCriteria(Criteria.where("organizationId").is(filter.getOrganizationId()));
        }
        if (filter.getSerialId() != null) {
            query.addCriteria(Criteria.where("serialId").is(filter.getSerialId()));
        }
        if (filter.getPartyAggregationId() != null) {
            query.addCriteria(Criteria.where("partyAggregationId").is(filter.getPartyAggregationId()));
        }
        if (filter.getUsed() != null) {
            query.addCriteria(Criteria.where("used").is(filter.getUsed()));
        }
        if (CollectionUtils.isNotEmpty(filter.getAggregationStatusList()) && filter.getAggregationStatus() == null) {
            query.addCriteria(Criteria.where("status").in(filter.getAggregationStatusList()));
        }
        if (filter.getFrom() != null && filter.getTo() != null) {
            query.addCriteria(Criteria.where("lastModifiedDate").gte(filter.getFrom())
                    .andOperator(Criteria.where("lastModifiedDate").lte(filter.getTo())));
        }
        if (filter.getAggregationStatus() != null) {
            query.addCriteria(Criteria.where("status").is(filter.getAggregationStatus()));
        }
        if (filter.getLogin() != null) {
            query.addCriteria(Criteria.where("login").regex(filter.getLogin().trim(), "i"));
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
        final long totalCount = mongoTemplate.count(query, Block.class);

        Sort.Direction direction = SortTypeEnum.asc.equals(filter.getSortOrder()) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sorting = Sort.by(direction, Objects.toString(filter.getOrderBy(), "id"));
        query.with(sorting);
        query.with(filter.getPageable());

        return new PageImpl<>(mongoTemplate.find(query, Block.class), filter.getPageable(), totalCount);
    }


    @Override
    public BigDecimal findAllByFilterStats(final MongoFilter filter) {
        final List<AggregationOperation> matches = new ArrayList<>();
        if (filter.getUnit() != null) {
            matches.add(new MatchOperation(Criteria.where("unit").is(filter.getUnit())));
        }
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
        if (filter.getSerialId() != null) {
            matches.add(new MatchOperation(Criteria.where("serialId").is(filter.getSerialId())));
        }
        if (filter.getUsed() != null) {
            matches.add(new MatchOperation(Criteria.where("used").is(filter.getUsed())));
        }
        if (filter.getPartyAggregationId() != null) {
            matches.add(new MatchOperation(Criteria.where("partyAggregationId").is(filter.getPartyAggregationId())));
        }
        if (filter.getAggregationStatus() != null) {
            matches.add(new MatchOperation(Criteria.where("status").is(filter.getAggregationStatus())));
        } else if (CollectionUtils.isNotEmpty(filter.getAggregationStatusList())) {
            matches.add(new MatchOperation(Criteria.where("status").in(filter.getAggregationStatusList())));
        }
        if (filter.getFrom() != null && filter.getTo() != null) {
            matches.add(new MatchOperation(Criteria.where("lastModifiedDate").gte(filter.getFrom())
                    .andOperator(Criteria.where("lastModifiedDate").lte(filter.getTo()))));
        }

        if (filter.getLogin() != null) {
            matches.add(new MatchOperation(Criteria.where("login").regex(filter.getLogin(), "i")));
        }
        if (filter.checkSearchIsEmpty()) {
            final Criteria searchCriteria = new Criteria();
            searchCriteria.orOperator(Criteria.where("printCode").regex(filter.getSearch(), "i"), Criteria.where("code").regex(filter.getSearch(), "i"));
            matches.add(new MatchOperation(searchCriteria));
        }

        final GroupOperation groupOperation = group("organizationId").sum("quantity").as("quantity");
        matches.add(groupOperation);

        final Aggregation aggregate = Aggregation.newAggregation(matches);

        final AggregationResults<Block> orderAggregate = mongoTemplate.aggregate(aggregate, Block.class, Block.class);

        return Optional.of(orderAggregate.getMappedResults())
                .filter(CollectionUtils::isNotEmpty)
                .map(List::getFirst)
                .map(Block::getQuantity)
                .map(BigDecimal::valueOf)
                .orElse(BigDecimal.ZERO);
    }

    @Override
    public List<Block> getPartyAggregations(String partyAggregationId) {
        final List<AggregationOperation> matches = new ArrayList<>();
        matches.add(new MatchOperation(Criteria.where("partyAggregationId").is(partyAggregationId).andOperator(Criteria.where("unit").is(PackageType.BLOCK))));
        matches.add(Aggregation.project("printCode", "status"));
        matches.add(Aggregation.sort(Sort.Direction.ASC, "createdDate"));

        final AggregationResults<Block> orderAggregate = mongoTemplate.aggregate(Aggregation.newAggregation(matches), Block.class, Block.class);
        return orderAggregate.getMappedResults();
    }
}
