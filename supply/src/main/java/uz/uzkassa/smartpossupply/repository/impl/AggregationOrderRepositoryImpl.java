package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import uz.uzkassa.smartpossupply.domain.aggregation.AggregationOrder;
import uz.uzkassa.smartpossupply.dto.aggregation.AggregationStatisticsDTO;
import uz.uzkassa.smartpossupply.filters.AggregationOrderFilter;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.repository.AggregationOrderRepositoryCustom;

import java.util.List;

public class AggregationOrderRepositoryImpl extends BaseFilterableRepositoryImpl<AggregationOrder, AggregationOrderFilter>
    implements AggregationOrderRepositoryCustom {

    @Override
    protected Class<AggregationOrder> getEntityClass() {
        return AggregationOrder.class;
    }

    @Override
    protected String getEntityAlias() {
        return "a";
    }

    @Override
    public void applyFilter(AggregationOrderFilter filter) {
        filter.setCompanyId(getCompanyId(filter));
    }

    @Override
    public String getBaseQuery(AggregationOrderFilter filter, boolean includeFetch) {
        StringBuilder sql = new StringBuilder(" FROM AggregationOrder a ");

        if (includeFetch) {
            sql.append("LEFT JOIN FETCH a.creator creator ")
                .append("LEFT JOIN FETCH a.forEmployee e ");
        } else {
            sql.append("LEFT JOIN a.creator creator ")
                .append("LEFT JOIN a.forEmployee e ");
        }

        // Base conditions
        sql.append("WHERE a.deleted = false ")
            .append("AND a.companyId = :companyId ");

        // Dynamic conditions
        appendCondition(sql, filter.getStatus() != null, "AND a.status = :status ");
        appendCondition(sql, filter.getPackageType() != null, "AND a.packageType = :packageType ");
        appendCondition(sql, filter.getForEmployee() != null, "AND a.forEmployeeId = :forEmployeeId ");
        appendCondition(sql, filter.getFrom() != null, "AND a.createdDate >= :from ");
        appendCondition(sql, filter.getTo() != null, "AND a.createdDate <= :to ");

        // Search conditions
        if (filter.isNotEmpty()) {
            sql.append("AND (")
                .append("LOWER(a.orderNumber)").append(filter.getSearchKey())
                .append(") ");
        }

        return sql.toString();
    }

    @Override
    public void setParameters(TypedQuery<?> query, AggregationOrderFilter filter) {
        // Required parameter
        query.setParameter("companyId", filter.getCompanyId());

        // Optional parameters
        setParameterIfNotNull(query, "status", filter.getStatus());
        setParameterIfNotNull(query, "packageType", filter.getPackageType());
        setParameterIfNotNull(query, "forEmployeeId", filter.getForEmployee());
        setParameterIfNotNull(query, "from", filter.getFrom());
        setParameterIfNotNull(query, "to", filter.getTo());

        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
        }
    }

    @Override
    public AggregationStatisticsDTO getStatistics(BaseFilter filter) {
        filter.setCompanyId(getCompanyId(filter));
        StringBuilder sql = new StringBuilder();
        sql.append(" select count(distinct ao.id) filter(where a.status='DRAFT') ");
        sql.append(" as draftTotal, ");
        sql.append(" count(distinct ao.id) filter(where a.status='ERROR') ");
        sql.append(" as errorTotal, ");
        sql.append(" count(distinct ao.id) filter(where a.status='EMPTY') ");
        sql.append(" as emptyTotal, ");
        sql.append(" count(distinct ao.id) filter(where a.status='CREATED') ");
        sql.append(" as createdTotal, ");
        sql.append(" count(distinct ao.id) filter(where a.status='UTILIZED') ");
        sql.append(" as utilizedTotal, ");
        sql.append(" count(distinct ao.id) filter(where a.status='RETURNED') ");
        sql.append(" as returnedTotal, ");
        sql.append(" count(distinct ao.id) filter(where a.status='SHIPPED') ");
        sql.append(" as shippedTotal ");
        sql.append(" from aggregation_order ao ");
        sql.append(" inner join aggregation a on ao.id = a.aggregation_order_id ");
        sql.append(" where ao.deleted =false ");
        sql.append(" and ao.company_id =:companyId ");

        if (filter.getFrom() != null) {
            sql.append(" and ao.created_date>=:from");
        }
        if (filter.getTo() != null) {
            sql.append(" and ao.created_date<=:to");
        }
        if (filter.isNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(ao.order_number)").append(filter.getSearchKey());
            sql.append(" or lower(ao.description)").append(filter.getSearchKey());
            sql.append(") ");
        }
        sql.append(" group by a.status");

        Query query = entityManager.createNativeQuery(sql.toString(), "AggregationOrderMapper");

        query.setParameter("companyId", filter.getCompanyId());

        if (filter.getFrom() != null) {
            query.setParameter("from", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("to", filter.getTo());
        }
        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
        }

        List<AggregationStatisticsDTO> list = query.getResultList();

        Long draftTotal = 0L;

        Long errorTotal = 0L;

        Long emptyTotal = 0L;

        Long createdTotal = 0L;

        Long utilizedTotal = 0L;

        Long returnedTotal = 0L;

        Long shippedTotal = 0L;

        for (AggregationStatisticsDTO aggregationStatisticsDTO : list) {
            draftTotal += aggregationStatisticsDTO.getDraftTotal();
            errorTotal += aggregationStatisticsDTO.getErrorTotal();
            emptyTotal += aggregationStatisticsDTO.getEmptyTotal();
            createdTotal += aggregationStatisticsDTO.getCreatedTotal();
            utilizedTotal += aggregationStatisticsDTO.getUtilizedTotal();
            returnedTotal += aggregationStatisticsDTO.getReturnedTotal();
            shippedTotal += aggregationStatisticsDTO.getShippedTotal();
        }
        return new AggregationStatisticsDTO(draftTotal, errorTotal, emptyTotal, createdTotal, utilizedTotal, returnedTotal, shippedTotal);
    }
}
