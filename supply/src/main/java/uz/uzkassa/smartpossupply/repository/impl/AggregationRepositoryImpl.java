package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.smartpossupply.domain.aggregation.Aggregation;
import uz.uzkassa.smartpossupply.dto.aggregation.AggregationStatisticsDTO;
import uz.uzkassa.smartpossupply.dto.aggregation.AggregationUsedStatisticsDTO;
import uz.uzkassa.smartpossupply.enums.AggregationStatus;
import uz.uzkassa.smartpossupply.enums.PackageType;
import uz.uzkassa.smartpossupply.filters.AggregationFilter;
import uz.uzkassa.smartpossupply.repository.AggregationRepositoryCustom;
import uz.uzkassa.smartpossupply.utils.QueryUtils;

import java.util.List;

public class AggregationRepositoryImpl extends BaseFilterableRepositoryImpl<Aggregation, AggregationFilter> implements AggregationRepositoryCustom {

    @Override
    protected Class<Aggregation> getEntityClass() {
        return Aggregation.class;
    }

    @Override
    protected String getEntityAlias() {
        return "a";
    }

    @Override
    public void applyFilter(AggregationFilter filter) {
        filter.setCompanyId(getCompanyId(filter));
    }

    @Override
    public String getBaseQuery(AggregationFilter filter, boolean includeFetch) {
        StringBuilder sql = new StringBuilder(" FROM Aggregation a ");

        // Conditional join for product (only when filtering by product)
        if (filter.getProductId() != null) {
            if (includeFetch) {
                sql.append("LEFT JOIN FETCH a.product p ");
                sql.append("LEFT JOIN FETCH a.stock s ");
                sql.append("LEFT JOIN FETCH a.productSerial ps ");
            } else {
                sql.append("LEFT JOIN a.product p ");
                sql.append("LEFT JOIN a.stock s ");
                sql.append("LEFT JOIN a.productSerial ps ");
            }
        }

        // Base conditions
        sql.append("WHERE a.deleted = false ");

        // IN clause for multiple statuses
        if (!CollectionUtils.isEmpty(filter.getStatuses())) {
            sql.append("AND a.status IN (").append(QueryUtils.getSqlFormat(filter.getStatuses())).append(") ");
        }

        sql.append("AND a.companyId = :companyId ");

        // Dynamic conditions
        appendCondition(sql, filter.getProductId() != null, "AND a.productId = :productId ");
        appendCondition(sql, filter.getAggregationOrderId() != null, "AND a.aggregationOrderId = :aggregationOrderId ");
        appendCondition(sql, filter.getMarkOrderPartyId() != null, "AND a.markOrderPartyId = :markOrderPartyId ");
        appendCondition(sql, StringUtils.isNotEmpty(filter.getStatus()), "AND a.status = :status ");
        appendCondition(sql, StringUtils.isNotEmpty(filter.getPackageType()), "AND a.packageType = :packageType ");
        appendCondition(sql, filter.getFrom() != null, "AND a.aggregationDate >= :fromDate ");
        appendCondition(sql, filter.getTo() != null, "AND a.aggregationDate <= :toDate ");
        appendCondition(sql, filter.getPartyProductId() != null, "AND a.partyProductId = :partyProductId ");
        appendCondition(sql, filter.getProductSerialId() != null, "AND a.productSerialId = :productSerialId ");

        // Search conditions across multiple fields
        if (filter.isNotEmpty()) {
            sql.append("AND (")
                .append("LOWER(a.code)").append(filter.getSearchKey())
                .append(" OR LOWER(a.originalCode)").append(filter.getSearchKey())
                .append(" OR LOWER(a.serialNumber)").append(filter.getSearchKey())
                .append(" OR LOWER(a.gcp)").append(filter.getSearchKey())
                .append(") ");
        }

        return sql.toString();
    }

    @Override
    public void setParameters(TypedQuery<?> query, AggregationFilter filter) {
        // Required parameter
        query.setParameter("companyId", filter.getCompanyId());

        // Optional parameters
        setParameterIfNotNull(query, "productId", filter.getProductId());
        setParameterIfNotNull(query, "aggregationOrderId", filter.getAggregationOrderId());
        setParameterIfNotNull(query, "markOrderPartyId", filter.getMarkOrderPartyId());
        setParameterIfNotNull(query, "partyProductId", filter.getPartyProductId());
        setParameterIfNotNull(query, "productSerialId", filter.getProductSerialId());

        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("status", AggregationStatus.valueOf(filter.getStatus()));
        }

        if (StringUtils.isNotEmpty(filter.getPackageType())) {
            query.setParameter("packageType", PackageType.valueOf(filter.getPackageType()));
        }

        setParameterIfNotNull(query, "fromDate", filter.getFrom());
        setParameterIfNotNull(query, "toDate", filter.getTo());

        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
        }
    }


    @Override
    public List<String> getCodesByAggregationOrder(AggregationFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("select a.originalCode from Aggregation a");
        sql.append(" where a.aggregationOrderId =:aggregationOrderId");
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and a.status=:status");
        }
        if (filter.isNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(a.code)").append(filter.getSearchKey());
            sql.append(" or lower(a.originalCode)").append(filter.getSearchKey());
            sql.append(" or lower(a.serialNumber)").append(filter.getSearchKey());
            sql.append(") ");
        }
        sql.append(" order by a.updatedDate desc");

        TypedQuery<String> query = entityManager.createQuery(sql.toString(), String.class);

        query.setParameter("aggregationOrderId", filter.getAggregationOrderId());
        if (filter.getStatus() != null) {
            query.setParameter("status", AggregationStatus.valueOf(filter.getStatus()));
        }
        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
        }

        return query.getResultList();
    }

    @Override
    public Long nextVal(String gtin) {
        return (
                (long) entityManager.createNativeQuery("select nextval('gcp_seq_" + gtin + "')").getSingleResult()
        );
    }

    @Override
    public AggregationStatisticsDTO getStatistics(AggregationFilter filter) {
        boolean hasProductFilter = filter.getProductId() != null;
        filter.setCompanyId(getCompanyId(filter));
        StringBuilder sql = new StringBuilder();
        sql.append(" select ");
        sql.append(" (case when a.status = 'DRAFT' then count(distinct a.id) else 0 end) as emittedTotal,");
        sql.append(" (case when  a.status = 'ERROR' then count(distinct a.id) else 0 end) as appliedTotal, ");
        sql.append(" (case when  a.status = 'EMPTY' then count(distinct a.id) else 0 end) as introducedTotal, ");
        sql.append(" (case when  a.status = 'CREATED' then count(distinct a.id) else 0 end) as writtenOFFTotal, ");
        sql.append(" (case when  a.status = 'UTILIZED' then count(distinct a.id) else 0 end) as withdrawnTotal, ");
        sql.append(" (case when  a.status = 'RETURNED' then count(distinct a.id) else 0 end) as reservedNotUsedTotal, ");
        sql.append(" (case when  a.status = 'SHIPPED' then count(distinct a.id) else 0 end) as introducedReturnedTotal ");
        sql.append(getBaseSql(filter, hasProductFilter));
        sql.append(" group by a.status ");

        TypedQuery<Object[]> query = entityManager.createQuery(sql.toString(), Object[].class);
        query.setParameter("companyId", filter.getCompanyId());

        if (filter.getAggregationOrderId() != null) {
            query.setParameter("aggregationOrderId", filter.getAggregationOrderId());
        }
        if (filter.getMarkOrderPartyId() != null) {
            query.setParameter("markOrderPartyId", filter.getMarkOrderPartyId());
        }
        if (filter.getStatus() != null) {
            query.setParameter("status", AggregationStatus.valueOf(filter.getStatus()));
        }
        if (filter.getPartyProductId() != null) {
            query.setParameter("partyProductId", filter.getPartyProductId());
        }
        if (filter.getPackageType() != null) {
            query.setParameter("packageType", PackageType.valueOf(filter.getPackageType()));
        }
        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getTo());
        }
        if (hasProductFilter) {
            query.setParameter("productId", filter.getProductId());
        }
        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
        }
        List<Object[]> result = query.getResultList();

        Long draftTotal = 0L;

        Long errorTotal = 0L;

        Long emptyTotal = 0L;

        Long createdTotal = 0L;

        Long utilizedTotal = 0L;

        Long returnedTotal = 0L;

        Long shippedTotal = 0L;

        for (Object[] row : result) {
            draftTotal += (Long) row[0];
            errorTotal += (Long) row[1];
            emptyTotal += (Long) row[2];
            createdTotal += (Long) row[3];
            utilizedTotal += (Long) row[4];
            returnedTotal += (Long) row[5];
            shippedTotal += (Long) row[6];
        }
        return new AggregationStatisticsDTO(draftTotal, errorTotal, emptyTotal, createdTotal, utilizedTotal, returnedTotal, shippedTotal);
    }

    public StringBuilder getBaseSql(AggregationFilter filter, boolean hasProductFilter) {
        StringBuilder sql = new StringBuilder();
        sql.append(" from Aggregation a ");
        if (hasProductFilter) {
            sql.append(" join a.product p ");
        }
        sql.append(" where a.deleted =false ");
        if (!CollectionUtils.isEmpty(filter.getStatuses())) {
            sql.append(" and a.status in (").append(QueryUtils.getSqlFormat(filter.getStatuses())).append(")");

        }
        sql.append(" and a.companyId =:companyId");
        if (hasProductFilter) {
            sql.append(" and a.productId =:productId");
        }
        if (filter.getAggregationOrderId() != null) {
            sql.append(" and a.aggregationOrderId =:aggregationOrderId");
        }
        if (filter.getMarkOrderPartyId() != null) {
            sql.append(" and a.markOrderPartyId =:markOrderPartyId");
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and a.status =:status");
        }
        if (StringUtils.isNotEmpty(filter.getPackageType())) {
            sql.append(" and a.packageType =:packageType");
        }
        if (filter.getFrom() != null) {
            sql.append(" and a.aggregationDate>=:fromDate ");
        }
        if (filter.getTo() != null) {
            sql.append(" and a.aggregationDate<=:toDate ");
        }
        if (filter.isNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(a.code)").append(filter.getSearchKey());
            sql.append(" or lower(a.originalCode)").append(filter.getSearchKey());
            sql.append(" or lower(a.serialNumber)").append(filter.getSearchKey());
            sql.append(" or lower(a.gcp)").append(filter.getSearchKey());
            sql.append(") ");
        }
        return sql;
    }

    @Override
    public List<AggregationUsedStatisticsDTO> getUsedCountByAggregationOrderIdIn(List<Long> idList) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select aggregation_order_id as aggregationOrderId, count(id) as quantity");
        sql.append(" from aggregation");
        sql.append(" where deleted is false ");
        sql.append(" and status <> :status");
        sql.append(" and aggregation_order_id in (:orders)");
        sql.append(" group by aggregation_order_id");
        Query query = entityManager
                .createNativeQuery(sql.toString(), "AggregationUsedStatisticsDTOMapper")
                .setParameter("status", AggregationStatus.DRAFT.name())
                .setParameter("orders", idList);

        return (List<AggregationUsedStatisticsDTO>) query.getResultList();
    }
}
