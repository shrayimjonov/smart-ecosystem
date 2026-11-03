package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import uz.uzkassa.cts.domain.stock.Stock;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.stock.balancereport.StockBalanceReportDetailListDTO;
import uz.uzkassa.cts.dto.stock.balancereport.StockBalanceReportStatisticItemDTO;
import uz.uzkassa.cts.dto.stock.balancereport.StockBalanceReportStatisticsDTO;
import uz.uzkassa.cts.enums.StockStatus;
import uz.uzkassa.cts.enums.StockTransferStatus;
import uz.uzkassa.cts.enums.StockTransferType;
import uz.uzkassa.cts.filter.StockFilter;
import uz.uzkassa.cts.repository.custom.StockRepositoryCustom;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class StockRepositoryImpl extends BaseRepositoryImpl implements StockRepositoryCustom {

    @Override
    public ResultList<Stock> getResultList(StockFilter filter) {
        ResultList<Stock> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select s from Stock s ");
        sql.append(" where s.deleted = false ");
        sql.append(" and s.companyId =:companyId ");
        sql.append(" and s.transferType =:transferType ");

        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and s.status =:status ");
        }
        if (filter.getBranchId() != null) {
            sql.append(" and s.branchId =:branchId ");
        }
        if (filter.getDepartmentId() != null) {
            sql.append(" and s.departmentId =:departmentId ");
        }
        if (filter.getWarehouseId() != null) {
            sql.append(" and s.warehouseId =:warehouseId ");
        }
        if (filter.getCustomerId() != null) {
            sql.append(" and s.customerId =:customerId ");
        }
        if (filter.getFrom() != null) {
            sql.append(" and s.stockDate >=:fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and s.stockDate <=:toDate");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(s.stockNumber) ").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select s", " select count(s.id)");
        sql.append(" order by s.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<Stock> query = entityManager
                .createQuery(sql.toString(), Stock.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        query.setParameter("companyId", getCompanyId(filter.getCompanyId()));
        countQuery.setParameter("companyId", getCompanyId(filter.getCompanyId()));

        query.setParameter("transferType", filter.getTransferType());
        countQuery.setParameter("transferType", filter.getTransferType());

        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("status", StockStatus.get(filter.getStatus()));
            countQuery.setParameter("status", StockStatus.get(filter.getStatus()));
        }
        if (filter.getBranchId() != null) {
            query.setParameter("branchId", filter.getBranchId());
            countQuery.setParameter("branchId", filter.getBranchId());
        }
        if (filter.getDepartmentId() != null) {
            query.setParameter("departmentId", filter.getDepartmentId());
            countQuery.setParameter("departmentId", filter.getDepartmentId());
        }
        if (filter.getWarehouseId() != null) {
            query.setParameter("warehouseId", filter.getWarehouseId());
            countQuery.setParameter("warehouseId", filter.getWarehouseId());
        }
        if (filter.getCustomerId() != null) {
            query.setParameter("customerId", filter.getCustomerId());
            countQuery.setParameter("customerId", filter.getCustomerId());
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
    public StockBalanceReportStatisticsDTO getBalanceReportStatistics(StockFilter filter) {
        StringBuilder sql = new StringBuilder();
        //stock-in
        sql.append(" select coalesce(sum(si.qty),0) as qty, 'STOCK_IN_TOTAL' as dataType");
        sql.append(getBalanceReportStatisticBaseSql(filter, StockTransferType.IN));

        sql.append(" union all");

        // stock-out
        sql.append(" select coalesce(sum(si.qty),0) as qty, 'STOCK_OUT_TOTAL' as dataType");
        sql.append(getBalanceReportStatisticBaseSql(filter, StockTransferType.OUT));

        sql.append(" union all");

        // stock-return
        sql.append(" select coalesce(sum(si.qty),0) as qty, 'STOCK_RETURN_TOTAL' as dataType");
        sql.append(getBalanceReportStatisticBaseSql(filter, StockTransferType.RETURN));

        sql.append(" union all");

        // stock-adjustment
        sql.append(" select coalesce(sum(si.qty),0) as qty, 'STOCK_ADJUSTMENT_TOTAL' as dataType");
        sql.append(" from stock_adjustment_item si ");
        sql.append(" inner join stock_adjustment s on s.id = si.stock_adjustment_id ");
        sql.append(" where s.deleted = false and si.deleted = false ");
        sql.append(" and si.item_id =").append(filter.getItemId());
        sql.append(" and s.status='").append(StockStatus.COMPLETED.getCode()).append("'");
        sql.append(" and s.warehouse_id =").append(filter.getWarehouseId());
        if (filter.getFrom() != null) {
            sql.append(" and s.adjustment_date >=:fromDate ");
        }
        if (filter.getTo() != null) {
            sql.append(" and s.adjustment_date <=:toDate ");
        }

        sql.append(" union all");

        // stock-transfer-plus
        sql.append(" select coalesce(sum(si.qty),0) as qty, 'STOCK_TRANSFER_TOTAL_PLUS' as dataType");
        sql.append(" from stock_transfer_item si ");
        sql.append(" inner join stock_transfer s on s.id = si.stock_transfer_id ");
        sql.append(" inner join warehouse tw on s.to_warehouse_id = tw.id ");
        sql.append(" where s.deleted = false and si.deleted = false ");
        sql.append(" and si.to_item_id = ").append(filter.getItemId());
        sql.append(" and s.status ='").append(StockTransferStatus.APPROVED.getCode()).append("'");
        sql.append(" and tw.id =").append(filter.getWarehouseId());
        if (filter.getFrom() != null) {
            sql.append(" and s.transfer_date>=:fromDate ");
        }
        if (filter.getTo() != null) {
            sql.append(" and s.transfer_date<=:toDate ");
        }
        //stock-transfer-minus
        sql.append(" union all");

        sql.append(" select coalesce(sum(si.qty),0) as qty, 'STOCK_TRANSFER_TOTAL_MINUS' as dataType");
        sql.append(" from stock_transfer_item si ");
        sql.append(" inner join stock_transfer s on s.id = si.stock_transfer_id ");
        sql.append(" inner join warehouse fw on s.from_warehouse_id = fw.id ");
        sql.append(" where s.deleted = false and si.deleted = false ");
        sql.append(" and si.from_item_id = ").append(filter.getItemId());
        sql.append(" and s.status='").append(StockTransferStatus.APPROVED.getCode()).append("'");
        sql.append(" and fw.id =").append(filter.getWarehouseId());
        if (filter.getFrom() != null) {
            sql.append(" and s.transfer_date>=:fromDate ");
        }
        if (filter.getTo() != null) {
            sql.append(" and s.transfer_date<=:toDate ");
        }
        Query query = entityManager.createNativeQuery(sql.toString(), "StockBalanceReportStatisticItemMapper");
        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getTo());
        }
        List<StockBalanceReportStatisticItemDTO> statistics = query.getResultList();
        StockBalanceReportStatisticsDTO result = new StockBalanceReportStatisticsDTO();
        statistics.forEach(stockProductStatisticsDTO -> {
            switch (stockProductStatisticsDTO.getDataType()) {
                case "STOCK_IN_TOTAL":
                    result.setStockInTotal(stockProductStatisticsDTO.getQty());
                    break;
                case "STOCK_OUT_TOTAL":
                    result.setStockOutTotal(stockProductStatisticsDTO.getQty());
                    break;
                case "STOCK_RETURN_TOTAL":
                    result.setStockReturnTotal(stockProductStatisticsDTO.getQty());
                    break;
                case "STOCK_ADJUSTMENT_TOTAL":
                    result.setStockAdjustmentTotal(stockProductStatisticsDTO.getQty());
                    break;
                case "STOCK_TRANSFER_TOTAL_PLUS":
                    result.setStockTransferToTotal(stockProductStatisticsDTO.getQty());
                    break;
                case "STOCK_TRANSFER_TOTAL_MINUS":
                    result.setStockTransferFromTotal(stockProductStatisticsDTO.getQty());
                    break;
            }
        });
        return result;
    }

    private StringBuilder getBalanceReportStatisticBaseSql(StockFilter filter, StockTransferType transferType) {
        StringBuilder sql = new StringBuilder();
        sql.append(" from stock_item si ");
        sql.append(" inner join stock s on s.id = si.stock_id ");
        sql.append(" where s.deleted = false and si.deleted = false ");
        sql.append(" and si.item_id =").append(filter.getItemId());
        sql.append(" and s.status ='").append(StockStatus.COMPLETED.getCode()).append("'");
        sql.append(" and s.warehouse_id =").append(filter.getWarehouseId());
        sql.append(" and s.transfer_type ='").append(transferType.name()).append("'");
        if (filter.getFrom() != null) {
            sql.append(" and s.stock_date >=:fromDate ");
        }
        if (filter.getTo() != null) {
            sql.append(" and s.stock_date <=:toDate ");
        }
        return sql;
    }

    @Override
    public Page<StockBalanceReportDetailListDTO> getBalanceReportDetails(StockFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("select s.id as id, s.stock_number stockNumber, s.stock_date as stockDate, si.qty as qty ");
        sql.append(getBalanceReportDetailsBaseSql(filter));
        sql.append(" order by s.id desc");

        StringBuilder countSql = new StringBuilder();
        countSql.append("select count(s.id) ");
        countSql.append(getBalanceReportDetailsBaseSql(filter));

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
        sql.append(" from stock_item si ");
        sql.append(" inner join stock s on s.id = si.stock_id ");
        sql.append(" inner join company c on c.id = s.company_id ");
        sql.append(" where s.deleted = false and si.deleted = false ");
        sql.append(" and si.item_id =").append(filter.getItemId());
        sql.append(" and s.transfer_type ='").append(filter.getTransferType()).append("'");
        sql.append(" and s.status ='").append(StockStatus.COMPLETED.getCode()).append("'");
        sql.append(" and s.warehouse_id =").append(filter.getWarehouseId());
        if (filter.getFrom() != null) {
            sql.append(" and s.transfer_date >=:fromDate ");
        }
        if (filter.getTo() != null) {
            sql.append(" and s.transfer_date <=:toDate ");
        }
        return sql;
    }
}
