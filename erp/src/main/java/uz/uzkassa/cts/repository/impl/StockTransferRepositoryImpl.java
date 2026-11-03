package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import uz.uzkassa.cts.domain.stock.StockTransfer;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.stock.balancereport.StockBalanceReportDetailListDTO;
import uz.uzkassa.cts.enums.StockTransferStatus;
import uz.uzkassa.cts.enums.StockTransferType;
import uz.uzkassa.cts.filter.StockFilter;
import uz.uzkassa.cts.filter.StockTransferFilter;
import uz.uzkassa.cts.repository.custom.StockTransferRepositoryCustom;

import java.util.ArrayList;

@SuppressWarnings("unused")
public class StockTransferRepositoryImpl extends BaseRepositoryImpl implements StockTransferRepositoryCustom {

    @Override
    public ResultList<StockTransfer> getResultList(StockTransferFilter filter) {
        ResultList<StockTransfer> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select st from StockTransfer st ");
        sql.append(" where st.deleted = false ");
        sql.append(" and st.companyId =:companyId ");

        if (filter.getFromBranchId() != null) {
            sql.append(" and st.fromBranchId =:fromBranchId ");
        }
        if (filter.getToBranchId() != null) {
            sql.append(" and st.toBranchId =:toBranchId ");
        }
        if (filter.getFromWarehouseId() != null) {
            sql.append(" and st.fromWarehouseId =:fromWarehouseId ");
        }
        if (filter.getToWarehouseId() != null) {
            sql.append(" and st.toWarehouseId =:toWarehouseId ");
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and st.status =:status ");
        }
        if (filter.getFrom() != null) {
            sql.append(" and st.transferDate >=:fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and st.transferDate <=:toDate");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(st.transferNumber) ").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select st", " select count(st.id)");
        sql.append(" order by st.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<StockTransfer> query = entityManager
                .createQuery(sql.toString(), StockTransfer.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        query.setParameter("companyId", getCompanyId(filter.getCompanyId()));
        countQuery.setParameter("companyId", getCompanyId(filter.getCompanyId()));

        if (filter.getFromBranchId() != null) {
            query.setParameter("fromBranchId", filter.getFromBranchId());
            countQuery.setParameter("fromBranchId", filter.getFromBranchId());
        }
        if (filter.getToBranchId() != null) {
            query.setParameter("toBranchId", filter.getToBranchId());
            countQuery.setParameter("toBranchId", filter.getToBranchId());
        }
        if (filter.getFromWarehouseId() != null) {
            query.setParameter("fromWarehouseId", filter.getFromWarehouseId());
            countQuery.setParameter("fromWarehouseId", filter.getFromWarehouseId());
        }
        if (filter.getToWarehouseId() != null) {
            query.setParameter("toWarehouseId", filter.getToWarehouseId());
            countQuery.setParameter("toWarehouseId", filter.getToWarehouseId());
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("status", StockTransferStatus.get(filter.getStatus()));
            countQuery.setParameter("status", StockTransferStatus.get(filter.getStatus()));
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
        sql.append(" select s.id as id, s.transfer_number as stockNumber, s.transfer_date as stockDate, si.qty as qty, fw.id as fromWarehouseId, fw.name as fromWarehouseName, tw.id as toWarehouseId, tw.name as toWarehouseName");
        sql.append(getQtyDetailsBaseSql(filter));
        StringBuilder countSql = new StringBuilder();
        countSql.append("select count(s.id) ");
        countSql.append(getQtyDetailsBaseSql(filter));
        sql.append(" order by s.id desc");

        Query query = entityManager
                .createNativeQuery(sql.toString(), "StockTransferItemQtyDetailListMapper")
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

    private StringBuilder getQtyDetailsBaseSql(StockFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append(" from stock_transfer_item si ");
        sql.append(" inner join stock_transfer s on s.id = si.stock_transfer_id ");
        sql.append(" inner join company c on s.company_id = c.id ");
        sql.append(" inner join warehouse fw on s.from_warehouse_id = fw.id ");
        sql.append(" inner join warehouse tw on s.to_warehouse_id = tw.id ");
        sql.append(" where si.deleted = false ");
        sql.append(" and s.status ='").append(StockTransferStatus.APPROVED.getCode()).append("'");
        if (StockTransferType.TRANSFER_TO == filter.getTransferType()) {
            sql.append(" and tw.id =").append(filter.getWarehouseId());
            sql.append(" and si.to_item_id = ").append(filter.getItemId());
        } else if (StockTransferType.TRANSFER_FROM == filter.getTransferType()) {
            sql.append(" and fw.id =").append(filter.getWarehouseId());
            sql.append(" and si.from_item_id =").append(filter.getItemId());
        }
        if (filter.getFrom() != null) {
            sql.append(" and s.transfer_date >=:fromDate ");
        }
        if (filter.getTo() != null) {
            sql.append(" and s.transfer_date <=:toDate ");
        }
        return sql;
    }
}
