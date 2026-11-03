package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.TypedQuery;
import org.springframework.util.StringUtils;
import uz.uzkassa.smartpossupply.domain.aggregation.AggregationBlock;
import uz.uzkassa.smartpossupply.enums.AggregationStatus;
import uz.uzkassa.smartpossupply.enums.MarkStatus;
import uz.uzkassa.smartpossupply.filters.AggregationBlockFilter;
import uz.uzkassa.smartpossupply.repository.AggregationBlockRepositoryCustom;

public class AggregationBlockRepositoryImpl
    extends BaseFilterableRepositoryImpl<AggregationBlock, AggregationBlockFilter> implements AggregationBlockRepositoryCustom {

    @Override
    protected Class<AggregationBlock> getEntityClass() {
        return AggregationBlock.class;
    }

    @Override
    protected String getEntityAlias() {
        return "a";
    }

    @Override
    public void applyFilter(AggregationBlockFilter filter) {
        filter.setCompanyId(getCompanyId(filter));
    }

    @Override
    public String getBaseQuery(AggregationBlockFilter filter, boolean includeFetch) {
        StringBuilder sql = new StringBuilder(" FROM AggregationBlock a ");

        // Add joins
        if (includeFetch) {
            sql.append("INNER JOIN FETCH a.creator creator ")
                .append("LEFT JOIN FETCH a.product p ");
        } else {
            sql.append("INNER JOIN a.creator creator ")
                .append("LEFT JOIN a.product p ");
        }

        // Base condition
        sql.append("WHERE a.companyId = :companyId ");

        // Dynamic conditions
        appendCondition(sql, filter.getParentId() != null, "AND a.parentId = :parentId ");
        appendCondition(sql, StringUtils.hasText(filter.getStatus()), "AND a.status = :status ");
        appendCondition(sql, StringUtils.hasText(filter.getTuronStatus()), "AND a.turonStatus = :turonStatus ");
        appendCondition(sql, filter.getMarkOrderPartyId() != null, "AND a.markOrderPartyId = :markOrderPartyId ");
        appendCondition(sql, filter.getMarkOrderId() != null, "AND a.markOrderId = :markOrderId ");
        appendCondition(sql, filter.getAggregationOrderId() != null, "AND a.aggregationOrderId = :aggregationOrderId ");
        appendCondition(sql, filter.getProductId() != null, "AND a.productId = :productId ");
        appendCondition(sql, filter.getPartyProductId() != null, "AND a.partyProductId = :partyProductId ");

        // Boolean condition
        if (filter.getUsed() != null) {
            sql.append("AND a.used = ").append(filter.getUsed()).append(" ");
        }

        // Search conditions
        if (filter.isNotEmpty()) {
            sql.append("AND (")
                .append("LOWER(a.code)").append(filter.getSearchKey())
                .append(" OR LOWER(a.printCode)").append(filter.getSearchKey())
                .append(") ");
        }

        return sql.toString();
    }

    @Override
    public void setParameters(TypedQuery<?> query, AggregationBlockFilter filter) {
        // Required parameter
        query.setParameter("companyId", filter.getCompanyId());

        // Optional parameters
        setParameterIfNotNull(query, "parentId", filter.getParentId());
        setParameterIfNotNull(query, "markOrderPartyId", filter.getMarkOrderPartyId());
        setParameterIfNotNull(query, "markOrderId", filter.getMarkOrderId());
        setParameterIfNotNull(query, "aggregationOrderId", filter.getAggregationOrderId());
        setParameterIfNotNull(query, "partyProductId", filter.getPartyProductId());
        setParameterIfNotNull(query, "productId", filter.getProductId());

        if (StringUtils.hasText(filter.getStatus())) {
            query.setParameter("status", AggregationStatus.valueOf(filter.getStatus()));
        }

        if (StringUtils.hasText(filter.getTuronStatus())) {
            query.setParameter("turonStatus", MarkStatus.valueOf(filter.getTuronStatus()));
        }

        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
        }
    }
}
