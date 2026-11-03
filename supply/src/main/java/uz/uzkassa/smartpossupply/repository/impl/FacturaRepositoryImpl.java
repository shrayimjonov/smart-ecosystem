package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import uz.uzkassa.smartpossupply.domain.factura.Factura;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.factura.FacturaStatisticsByStatusDTO;
import uz.uzkassa.smartpossupply.dto.factura.FacturaStatisticsDTO;
import uz.uzkassa.smartpossupply.dto.factura.Status;
import uz.uzkassa.smartpossupply.enums.DateType;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.filters.FacturaFilter;
import uz.uzkassa.smartpossupply.filters.FacturaStatisticsFilter;
import uz.uzkassa.smartpossupply.repository.FacturaRepositoryCustom;
import uz.uzkassa.smartpossupply.utils.DateUtils;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public class FacturaRepositoryImpl extends BaseFilterableRepositoryImpl<Factura, FacturaFilter> implements FacturaRepositoryCustom {

    @Override
    protected Class<Factura> getEntityClass() {
        return Factura.class;
    }

    @Override
    protected String getEntityAlias() {
        return "f";
    }

    @Override
    public void applyFilter(FacturaFilter filter) {
        if (!filter.isIncoming()) {
            filter.setBranchId(getBranchId(filter, false));
        }
    }

    @Override
    public String getBaseQuery(FacturaFilter filter, boolean includeFetch) {
        StringBuilder sql = new StringBuilder(" FROM Factura f ");

        // Add joins
        if (includeFetch) {
            sql.append("JOIN FETCH f.buyer b ")
                .append("JOIN FETCH f.seller s ")
                .append("LEFT JOIN FETCH f.branch branch ")
                .append("LEFT JOIN FETCH f.stockIn stockIn ")
                .append("LEFT JOIN FETCH f.stock stock ");
        } else {
            sql.append("JOIN f.buyer b ")
                .append("JOIN f.seller s ");
        }

        // Base condition
        sql.append("WHERE f.deleted = false ");

        // Dynamic conditions
        appendCondition(sql, filter.getBuyerId() != null, "AND b.id = :buyerId ");
        appendCondition(sql, filter.getSellerId() != null, "AND s.id = :sellerId ");

        if (filter.getSellerId() == null && filter.getBuyerId() == null) {
            sql.append("AND f.outgoing = :outgoing ");
        }

        appendCondition(sql, filter.getBranchId() != null, "AND f.branchId = :branchId ");
        appendCondition(sql, filter.getFacturaType() != null, "AND f.facturaType = :facturaType ");
        appendCondition(sql, !CollectionUtils.isEmpty(filter.getFacturaTypes()), "AND f.facturaType IN (:facturaTypes) ");

        // Status conditions (mutually exclusive)
        if (StringUtils.hasText(filter.getStatus())) {
            sql.append("AND f.status = :status ");
        } else if (StringUtils.hasText(filter.getIncludeStatuses())) {
            sql.append("AND f.status IN (:statuses) ");
        }

        appendCondition(sql, StringUtils.hasText(filter.getExcludeStatus()), "AND f.status != :excludeStatus ");

        // Date conditions with dynamic field selection
        if (filter.getFrom() != null || filter.getTo() != null) {
            String dateField = filter.isAdmin() ? "f.sendDate" : "f.facturaDate";

            if (filter.getFrom() != null) {
                sql.append("AND ").append(dateField).append(" >= :fromDate ");
            }
            if (filter.getTo() != null) {
                sql.append("AND ").append(dateField).append(" <= :toDate ");
            }
        }

        appendCondition(sql, filter.getStockId() != null, "AND f.stockId = :stockId ");

        // Search conditions
        if (filter.isNotEmpty()) {
            sql.append("AND (")
                .append("LOWER(f.facturaNo)")
                .append(filter.getSearchKey())
                .append(" OR LOWER(b.tin)")
                .append(filter.getSearchKey())
                .append(" OR LOWER(b.name)")
                .append(filter.getSearchKey())
                .append(" OR LOWER(s.tin)")
                .append(filter.getSearchKey())
                .append(" OR LOWER(s.name)")
                .append(filter.getSearchKey())
                .append(" OR LOWER(f.facturaId)")
                .append(filter.getSearchKey())
                .append(") ");
        }

        return sql.toString();
    }

    @Override
    public void setParameters(TypedQuery<?> query, FacturaFilter filter) {
        // Optional parameters
        setParameterIfNotNull(query, "buyerId", filter.getBuyerId());
        setParameterIfNotNull(query, "sellerId", filter.getSellerId());

        if (filter.getBuyerId() == null && filter.getSellerId() == null) {
            query.setParameter("outgoing", !filter.isIncoming());
        }

        setParameterIfNotNull(query, "branchId", filter.getBranchId());
        setParameterIfNotNull(query, "facturaType", filter.getFacturaType());

        if (!CollectionUtils.isEmpty(filter.getFacturaTypes())) {
            query.setParameter("facturaTypes", filter.getFacturaTypes());
        }

        // Status parameters (mutually exclusive)
        if (StringUtils.hasText(filter.getStatus())) {
            query.setParameter("status", Status.valueOf(filter.getStatus()));
        } else if (StringUtils.hasText(filter.getIncludeStatuses())) {
            List<Status> statuses = Arrays.stream(filter.getIncludeStatuses().split(",")).map(Status::valueOf).toList();
            query.setParameter("statuses", statuses);
        }

        if (StringUtils.hasText(filter.getExcludeStatus())) {
            query.setParameter("excludeStatus", Status.valueOf(filter.getExcludeStatus()));
        }

        // Date parameters with admin check
        if (filter.getFrom() != null) {
            Object fromDate = filter.isAdmin() ? filter.getFrom() : filter.getFrom().toLocalDate();
            query.setParameter("fromDate", fromDate);
        }

        if (filter.getTo() != null) {
            Object toDate = filter.isAdmin() ? filter.getTo() : filter.getTo().toLocalDate();
            query.setParameter("toDate", toDate);
        }

        setParameterIfNotNull(query, "stockId", filter.getStockId());

        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
        }
    }

    /**
     * Override to use custom ordering (facturaDate DESC)
     */
    @Override
    public ResultList<Factura> getResultList(FacturaFilter filter) {
        applyFilter(filter);

        // Early return if no results
        Long count = getCount(filter, entityManager);
        if (count == 0) {
            return new ResultList<>();
        }

        // Build query with custom ordering
        String sql = String.format("SELECT %s ", getEntityAlias()) + getBaseQuery(filter, true) + " ORDER BY f.facturaDate DESC";

        TypedQuery<Factura> query = entityManager.createQuery(sql, Factura.class).setFirstResult(filter.getStart()).setMaxResults(filter.getSize());

        setParameters(query, filter);

        ResultList<Factura> resultList = new ResultList<>();
        resultList.setCount(count);
        resultList.setList(query.getResultList());

        return resultList;
    }

    @Override
    public ResultList<Factura> getListTo1C(FacturaFilter filter) {
        filter.setTin(SecurityUtils.getCurrentCompanyTin());
        ResultList<Factura> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select f from Factura f");
        if (filter.isIncoming()) {
            sql.append(" join f.buyer c");
        } else {
            sql.append(" join f.seller c");
        }
        if (filter.getWarehouseId() != null) {
            sql.append(" join f.facturaDetails fd");
            sql.append(" join fd.products p");
        }
        sql.append(" where f.deleted = false");
        sql.append(" and c.tin =:tin");

        if (StringUtils.hasText(filter.getStatus())) {
            sql.append(" and f.status=:status");
        }
        if (filter.getBranchId() != null) {
            sql.append(" and f.branchId = :branchId");
        }
        if (filter.getWarehouseId() != null) {
            sql.append(" and p.warehouseId = :warehouseId");
        }
        if (filter.getFrom() != null) {
            sql.append(" and f.facturaDate >= :fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and f.facturaDate <= :toDate");
        }
        if (filter.isNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(f.facturaNo)").append(filter.getSearchKey());
            sql.append(" or lower(f.facturaId)").append(filter.getSearchKey());
            sql.append(")");
        }

        String countSql = sql.toString().replaceFirst("select f", "select count(f.id)");

        sql.append(" order by");
        if ("facturaDate".equalsIgnoreCase(filter.getOrderBy())) {
            sql.append(" f.facturaDate");
        } else if ("facturaNo".equalsIgnoreCase(filter.getOrderBy())) {
            sql.append(" f.facturaNo");
        } else if ("sendDate".equalsIgnoreCase(filter.getOrderBy())) {
            sql.append(" f.sendDate");
        } else if ("approvedDate".equalsIgnoreCase(filter.getOrderBy())) {
            sql.append(" f.approvedDate");
        } else if ("total".equalsIgnoreCase(filter.getOrderBy())) {
            sql.append(" f.total");
        } else {
            sql.append(" f.id ");
        }
        sql.append(filter.getSortOrder().getName());

        TypedQuery<Factura> query = entityManager.createQuery(sql.toString(), Factura.class).setFirstResult(filter.getStart()).setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        query.setParameter("tin", filter.getTin());
        countQuery.setParameter("tin", filter.getTin());

        if (StringUtils.hasText(filter.getStatus())) {
            query.setParameter("status", Status.valueOf(filter.getStatus()));
            countQuery.setParameter("status", Status.valueOf(filter.getStatus()));
        }
        if (filter.getBranchId() != null) {
            query.setParameter("branchId", filter.getBranchId());
            countQuery.setParameter("branchId", filter.getBranchId());
        }
        if (filter.getWarehouseId() != null) {
            query.setParameter("warehouseId", filter.getWarehouseId());
            countQuery.setParameter("warehouseId", filter.getWarehouseId());
        }
        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFrom().toLocalDate());
            countQuery.setParameter("fromDate", filter.getFrom().toLocalDate());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getTo().toLocalDate());
            countQuery.setParameter("toDate", filter.getTo().toLocalDate());
        }
        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
            countQuery.setParameter("searchKey", filter.getSearchValue());
        }

        Long count = countQuery.getSingleResult();
        if (count > 0) {
            resultList.setList(query.getResultList());
            resultList.setCount(count);
        }

        return resultList;
    }

    public List<FacturaStatisticsDTO> getFacturaStatistics(FacturaStatisticsFilter filter) {
        filter.setDateType(Optional.ofNullable(filter.getDateType()).orElseGet(DateType.DAY::getCode));
        String groupBy = "";
        String mapper = "";
        String orderBy = "";
        String byDay = "extract(day from f.factura_date)  as byDay, ";
        String byWeek = "extract(week from f.factura_date)  as byWeek, ";
        String byMonth = "extract(month from f.factura_date) as byMonth ,";
        String byYear = "extract(year from f.factura_date) as byYear ";

        StringBuilder sql = new StringBuilder();
        sql.append(" select ");
        sql.append(" sum(f.total) as amount, ");

        switch (DateType.valueOf(filter.getDateType())) {
            case DAY:
                sql.append(byDay);
                sql.append(byWeek);
                sql.append(byMonth);
                sql.append(byYear);
                groupBy = "group by byMonth, byYear, byDay , byWeek ";
                mapper = "FacturaStatisticByDayMapper";
                orderBy = "byYear, byMonth, byDay ";
                break;
            case WEEK:
                sql.append(byWeek);
                sql.append(byMonth);
                sql.append(byYear);
                groupBy = "group by byMonth, byYear, byWeek ";
                mapper = "FacturaStatisticByWeekMapper";
                orderBy = "byYear, byMonth, byWeek ";
                break;
            case MONTH:
                sql.append(byMonth);
                sql.append(byYear);
                groupBy = "group by byMonth, byYear ";
                mapper = "FacturaStatisticByMonthMapper";
                orderBy = "byYear, byMonth ";
                break;
        }

        sql.append(" from factura f ");
        sql.append(" inner join company c on c.id = f.company_id ");
        sql.append(" where f.deleted = false ");
        if (filter.getFrom() != null && filter.getTo() != null) {
            sql.append(" and f.factura_date between :fromDate and :toDate ");
        }
        sql.append(" and f.status not in ('DRAFT','CANCELLED') ");
        sql.append(" and c.id =:companyId ");
        if (filter.getBranchId() != null) {
            sql.append(" and f.branch_id=:branchId ");
        }
        sql.append(groupBy);
        sql.append(" order by ").append(orderBy);

        Query query = entityManager.createNativeQuery(sql.toString(), mapper);
        query.setParameter("companyId", SecurityUtils.getCurrentCompanyId());
        if (filter.getBranchId() != null) {
            query.setParameter("branchId", filter.getBranchId());
        }
        if (filter.getFrom() != null && filter.getTo() != null) {
            query.setParameter("fromDate", filter.getFrom());
            query.setParameter("toDate", filter.getTo());
        }

        return query.getResultList();
    }

    @Override
    public FacturaStatisticsByStatusDTO getStatistics(FacturaFilter filter) {
        applyFilter(filter);

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append("(CASE WHEN f.status='DRAFT' THEN COUNT(DISTINCT f.id) ELSE 0 END) AS draftTotal, ");
        sql.append("(CASE WHEN f.status='SENT' THEN COUNT(DISTINCT f.id) ELSE 0 END) AS sentTotal, ");
        sql.append("(CASE WHEN f.status='QUEUED' THEN COUNT(DISTINCT f.id) ELSE 0 END) AS queuedTotal, ");
        sql.append("(CASE WHEN f.status='PENDING' THEN COUNT(DISTINCT f.id) ELSE 0 END) AS pendingTotal, ");
        sql.append("(CASE WHEN f.status='CANCELLED' THEN COUNT(DISTINCT f.id) ELSE 0 END) AS cancelledTotal, ");
        sql.append("(CASE WHEN f.status='AGENT_ACCEPTED' THEN COUNT(DISTINCT f.id) ELSE 0 END) AS agentAcceptedTotal, ");
        sql.append("(CASE WHEN f.status='AGENT_REJECTED' THEN COUNT(DISTINCT f.id) ELSE 0 END) AS agentRejectedTotal, ");
        sql.append("(CASE WHEN f.status='ACCEPTED' THEN COUNT(DISTINCT f.id) ELSE 0 END) AS acceptedTotal, ");
        sql.append("(CASE WHEN f.status='REJECTED' THEN COUNT(DISTINCT f.id) ELSE 0 END) AS rejectedTotal, ");
        sql.append("(CASE WHEN f.status='REVOKED' THEN COUNT(DISTINCT f.id) ELSE 0 END) AS revokedTotal, ");
        sql.append("(CASE WHEN f.status='ERROR' THEN COUNT(DISTINCT f.id) ELSE 0 END) AS errorTotal ");
        sql.append(getBaseQuery(filter, false));
        sql.append(" GROUP BY f.status");

        TypedQuery<Object[]> query = entityManager.createQuery(sql.toString(), Object[].class);
        setParameters(query, filter);

        List<Object[]> result = query.getResultList();

        Long draftTotal = 0L;
        Long sentTotal = 0L;
        Long queuedTotal = 0L;
        Long pendingTotal = 0L;
        Long cancelledTotal = 0L;
        Long agentAcceptedTotal = 0L;
        Long agentRejectedTotal = 0L;
        Long acceptedTotal = 0L;
        Long rejectedTotal = 0L;
        Long revokedTotal = 0L;
        Long errorTotal = 0L;

        for (Object[] row : result) {
            draftTotal += (Long) row[0];
            sentTotal += (Long) row[1];
            queuedTotal += (Long) row[2];
            pendingTotal += (Long) row[3];
            cancelledTotal += (Long) row[4];
            agentAcceptedTotal += (Long) row[5];
            agentRejectedTotal += (Long) row[6];
            acceptedTotal += (Long) row[7];
            rejectedTotal += (Long) row[8];
            revokedTotal += (Long) row[9];
            errorTotal += (Long) row[10];
        }

        return new FacturaStatisticsByStatusDTO(draftTotal, sentTotal, queuedTotal, pendingTotal, cancelledTotal, agentAcceptedTotal, agentRejectedTotal, acceptedTotal, rejectedTotal, revokedTotal, errorTotal);
    }

    @Override
    public Long getMonthStatistics(BaseFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(f.id) FROM Factura f ");
        sql.append(" JOIN f.seller seller ");
        sql.append(" WHERE (f.sendDate between :fromDate AND :toDate) ");
        sql.append(" AND f.status !=:status  ");
        sql.append(" AND seller.tin NOT IN (:tins) ");
        sql.append(" AND f.companyId IS NOT NULL ");

        TypedQuery<Long> query = entityManager.createQuery(sql.toString(), Long.class);
        query.setParameter("fromDate", Optional.ofNullable(filter.getFrom()).orElseGet(DateUtils::getStartOfMonth));
        query.setParameter("toDate", Optional.ofNullable(filter.getTo()).orElseGet(DateUtils::getEndOfMonth));
        query.setParameter("status", Status.DRAFT);
        query.setParameter("tins", List.of("306351564", "306344019", "306866358"));
        return query.getSingleResult();
    }
}
