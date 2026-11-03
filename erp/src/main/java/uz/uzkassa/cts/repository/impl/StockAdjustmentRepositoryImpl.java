package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.stock.StockAdjustment;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.stock.balancereport.StockBalanceReportDetailListDTO;
import uz.uzkassa.cts.enums.StockStatus;
import uz.uzkassa.cts.filter.StockFilter;
import uz.uzkassa.cts.repository.custom.StockAdjustmentRepositoryCustom;

import java.util.ArrayList;

@Component
public class StockAdjustmentRepositoryImpl extends BaseRepositoryImpl implements StockAdjustmentRepositoryCustom {

    @Override
    public ResultList<StockAdjustment> getResultList(StockFilter filter) {
        ResultList<StockAdjustment> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select sa from StockAdjustment sa ");
        sql.append(" where sa.deleted = false ");
        sql.append(" and sa.companyId =:companyId ");

        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and sa.status =:status ");
        }
        if (filter.getBranchId() != null) {
            sql.append(" and sa.branchId =:branchId ");
        }
        if (filter.getFrom() != null) {
            sql.append(" and sa.adjustmentDate >=:fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and sa.adjustmentDate <=:toDate");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(sa.stockNumber) ").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select sa", " select count(sa.id)");
        sql.append(" order by sa.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<StockAdjustment> query = entityManager
                .createQuery(sql.toString(), StockAdjustment.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        query.setParameter("companyId", getCompanyId(filter.getCompanyId()));
        countQuery.setParameter("companyId", getCompanyId(filter.getCompanyId()));

        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("status", StockStatus.get(filter.getStatus()));
            countQuery.setParameter("status", StockStatus.get(filter.getStatus()));
        }
        if (filter.getBranchId() != null) {
            query.setParameter("branchId", filter.getBranchId());
            countQuery.setParameter("branchId", filter.getBranchId());
        }
        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFromStartOfDay());
            countQuery.setParameter("fromDate", filter.getFromStartOfDay());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getToEndOfDay());
            countQuery.setParameter("toDate", filter.getToEndOfDay());
        }
        if (filter.isSearchNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchFor());
            countQuery.setParameter("searchKey", filter.getSearchFor());
        }
        Long count = countQuery.getSingleResult();
        if (count > 0) {
            resultList.setList(query.getResultList());
            resultList.setCount(count);
        }
        return resultList;
    }

    @Override
    public Page<StockBalanceReportDetailListDTO> getBalanceReportDetails(StockFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("select s.id as id, s.adjustment_number as stockNumber, s.adjustment_date as stockDate, si.qty as qty ");
        sql.append(getBalanceReportDetailsBaseSql(filter));

        StringBuilder countSql = new StringBuilder();
        countSql.append("select count(s.id) ");
        countSql.append(getBalanceReportDetailsBaseSql(filter));
        sql.append(" order by s.id desc");

        Query query = entityManager
                .createNativeQuery(sql.toString(), "StockBalanceReportDetailListMapper")
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        Query countQuery = entityManager.createNativeQuery(countSql.toString());

        if (filter.getFrom() != null) {
            countQuery.setParameter("fromDate", filter.getFrom());
            query.setParameter("fromDate", filter.getFrom());
        }
        if (filter.getTo() != null) {
            countQuery.setParameter("toDate", filter.getTo());
            query.setParameter("toDate", filter.getTo());
        }
        long count = (long) countQuery.getSingleResult();
        if (count > 0) {
            return new PageImpl<>(query.getResultList(), filter.getOrderedPageable(), count);
        }
        return new PageImpl<>(new ArrayList<>(), filter.getOrderedPageable(), count);
    }

    private StringBuilder getBalanceReportDetailsBaseSql(StockFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append(" from stock_adjustment_item si ");
        sql.append(" inner join stock_adjustment s on s.id = si.stock_adjustment_id ");
        sql.append(" inner join company c on s.company_id = c.id ");
        sql.append(" where s.deleted = false and si.deleted = false ");
        sql.append(" and si.item_id =").append(filter.getItemId());
        sql.append(" and s.status ='").append(StockStatus.COMPLETED.getCode()).append("'");
        sql.append(" and s.warehouse_id =").append(filter.getWarehouseId());

        if (filter.getFrom() != null) {
            sql.append(" and s.adjustment_date>=:fromDate ");
        }
        if (filter.getTo() != null) {
            sql.append(" and s.adjustment_date<=:toDate ");
        }
        return sql;
    }
}
