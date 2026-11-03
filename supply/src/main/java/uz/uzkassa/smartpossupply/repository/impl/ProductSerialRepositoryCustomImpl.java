package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import uz.uzkassa.smartpossupply.domain.productserial.ProductSerial;
import uz.uzkassa.smartpossupply.dto.stock.StockProductQtyDetailListDTO;
import uz.uzkassa.smartpossupply.dto.stock.StockProductQtyStatisticsDTO;
import uz.uzkassa.smartpossupply.dto.stockreport.StockProductStatisticsDTO;
import uz.uzkassa.smartpossupply.enums.StockStatus;
import uz.uzkassa.smartpossupply.enums.StockTransferStatus;
import uz.uzkassa.smartpossupply.enums.TransferType;
import uz.uzkassa.smartpossupply.filters.ProductSerialFilter;
import uz.uzkassa.smartpossupply.filters.StockFilter;
import uz.uzkassa.smartpossupply.repository.ProductSerialRepositoryCustom;

import java.util.ArrayList;
import java.util.List;

public class ProductSerialRepositoryCustomImpl extends BaseRepositoryImpl implements ProductSerialRepositoryCustom {

    @Override
    public Page<ProductSerial> getResultList(ProductSerialFilter filter) {
        filter.setCompanyId(getCompanyId(filter));

        StringBuilder sql = new StringBuilder();
        sql.append("select distinct ps ");
        sql.append(" from ProductSerial ps ");
        if (filter.isHideZero() || filter.getWarehouseId() != null) {
            sql.append(" join ProductSerialWarehouse psw on ps.id=psw.productSerialId");
        }
        sql.append(" join ps.product p ");
        sql.append(" where ps.deleted =false");
        sql.append(" and ps.companyId =:companyId");

        if (filter.getProductId() != null) {
            sql.append(" and p.id =:productId");
        }
        if (filter.getBranchId() != null) {
            sql.append(" and p.branchId=:branchId");
        }
        if (filter.getWarehouseId() != null) {
            sql.append(" and psw.warehouseId=:warehouseId");
        }
        if (filter.isNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(ps.serialNumber)").append(filter.getSearchKey());
            sql.append(" or lower(p.name)").append(filter.getSearchKey());
            sql.append(" or lower(p.barcode)").append(filter.getSearchKey());
            sql.append(" or lower(p.vatBarcode)").append(filter.getSearchKey());
            sql.append(") ");
        }

        String countSql = sql.toString().replaceFirst("select distinct ps", "select count(distinct ps.id)");

        sql.append(" order by ps.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<ProductSerial> query = entityManager
                .createQuery(sql.toString(), ProductSerial.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        query.setParameter("companyId", filter.getCompanyId());
        countQuery.setParameter("companyId", filter.getCompanyId());

        if (filter.getProductId() != null) {
            query.setParameter("productId", filter.getProductId());
            countQuery.setParameter("productId", filter.getProductId());
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
            query.setParameter("fromDate", filter.getFrom());
            countQuery.setParameter("fromDate", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getTo());
            countQuery.setParameter("toDate", filter.getTo());
        }
        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
            countQuery.setParameter("searchKey", filter.getSearchValue());
        }
        return new PageImpl<>(query.getResultList(), filter.getSortedPageable(), countQuery.getSingleResult());
    }


    @Override
    public StockProductQtyStatisticsDTO getQtyStatistics(StockFilter filter) {
        StringBuilder sql = new StringBuilder();
        //stock-in
        sql.append(" select coalesce(sum(sp.qty),0) as qty, 'STOCK_IN_TOTAL' as dataType");
        sql.append(getStatisticsBaseSql(filter, TransferType.IN));
        sql.append(" union all");
        // stock-out
        sql.append(" select coalesce(sum(sp.qty),0) as qty, 'STOCK_OUT_TOTAL' as dataType");
        sql.append(getStatisticsBaseSql(filter, TransferType.OUT));
        sql.append(" union all");
        // stock-return
        sql.append(" select coalesce(sum(sp.qty),0) as qty, 'STOCK_RETURN_TOTAL' as dataType");
        sql.append(getStatisticsBaseSql(filter, TransferType.RETURN));
        sql.append(" union all");
        //stock-adjustment
        sql.append(" select coalesce(sum(sp.qty),0) as qty, 'STOCK_ADJUSTMENT_TOTAL' as dataType");
        sql.append(" from stock_adjustment_product sp ");
        sql.append(" inner join stock_adjustment s on s.id = sp.stock_adjustment_id ");
        sql.append(" inner join product p on sp.product_id = p.id ");
        sql.append(" where sp.deleted =false and p.deleted =false ");
        sql.append(" and sp.product_serial_id = ").append(filter.getProductSerialId());
        sql.append(" and s.status='").append(StockStatus.COMPLETED.getCode()).append("'");
        sql.append(" and s.warehouse_id=").append(filter.getWarehouseId());
        if (filter.getFrom() != null) {
            sql.append(" and s.adjustment_date>=:fromDate ");
        }
        if (filter.getTo() != null) {
            sql.append(" and s.adjustment_date<=:toDate ");
        }
        sql.append(" union all");
        // stock-transfer-plus
        sql.append(" select coalesce(sum(sp.qty),0) as qty, 'STOCK_TRANSFER_TOTAL_PLUS' as dataType");
        sql.append(" from stock_transfer_product sp ");
        sql.append(" inner join stock_transfer s on s.id = sp.stock_transfer_id ");
        sql.append(" inner join product tp on tp.id = sp.to_product_id ");
        sql.append(" inner join warehouse tw on s.to_warehouse_id = tw.id ");
        sql.append(" where sp.deleted =false ");
        sql.append(" and sp.product_serial_id = ").append(filter.getProductSerialId());
        sql.append(" and s.status='").append(StockTransferStatus.APPROVED.getCode()).append("'");
        sql.append(" and tw.id=").append(filter.getWarehouseId());
        if (filter.getFrom() != null) {
            sql.append(" and s.transfer_date>=:fromDate ");
        }
        if (filter.getTo() != null) {
            sql.append(" and s.transfer_date<=:toDate ");
        }
        //stock-transfer-minus
        sql.append(" union all");
        sql.append(" select coalesce(sum(sp.qty),0) as qty, 'STOCK_TRANSFER_TOTAL_MINUS' as dataType");
        sql.append(" from stock_transfer_product sp ");
        sql.append(" inner join stock_transfer s on s.id = sp.stock_transfer_id ");
        sql.append(" inner join product fp on fp.id = sp.from_product_id");
        sql.append(" inner join warehouse fw on s.from_warehouse_id = fw.id ");
        sql.append(" where sp.deleted =false ");
        sql.append(" and sp.product_serial_id = ").append(filter.getProductSerialId());
        sql.append(" and s.status='").append(StockTransferStatus.APPROVED.getCode()).append("'");
        sql.append(" and fw.id=").append(filter.getWarehouseId());


        Query query = entityManager.createNativeQuery(sql.toString(), "StockProductQtyStatisticsMapper");
        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getTo());
        }
        List<StockProductStatisticsDTO> statistics = query.getResultList();

        StockProductQtyStatisticsDTO result = new StockProductQtyStatisticsDTO();
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

    private StringBuilder getStatisticsBaseSql(StockFilter filter, TransferType transferType) {
        StringBuilder sql = new StringBuilder();
        sql.append(" from stock s ");
        sql.append(" inner join company c ");
        if (TransferType.OUT.equals(filter.getTransferType())) {
            sql.append(" on s.customer_id = c.id ");
        } else if (TransferType.IN.equals(filter.getTransferType())) {
            sql.append(" on s.supplier_id = c.id ");
        } else {
            sql.append(" on s.company_id = c.id ");
        }
        sql.append(" inner join stock_product sp on sp.stock_id = s.id ");
        sql.append(" inner join product_serial ps on ps.id = sp.product_serial_id ");
        sql.append(" where s.deleted = false and sp.deleted = false and ps.deleted = false ");
        sql.append(" and s.stock_transfer_id is null");
        sql.append(" and ps.id = ").append(filter.getProductSerialId());
        sql.append(" and s.status='").append(StockStatus.COMPLETED.getCode()).append("'");
        if (filter.getWarehouseId() != null) {
            sql.append(" and (s.warehouse_id=").append(filter.getWarehouseId());
            sql.append(" or sp.warehouse_id=").append(filter.getWarehouseId());
            sql.append(" )");
        }
        sql.append(" and s.transfer_type='").append(transferType.name()).append("'");
        if (filter.getFrom() != null) {
            sql.append(" and s.transfer_date>=:fromDate ");
        }
        if (filter.getTo() != null) {
            sql.append(" and s.transfer_date<=:toDate ");
        }
        return sql;
    }

    @Override
    public Page<StockProductQtyDetailListDTO> getQtyDetails(StockFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("select s.id as id, s.stock_number stockNumber, s.transfer_date as stockDate, s.completed_date as stockCompletedDate, sp.qty as qty, c.id as companyId, c.name as companyName, c.tin as companyTin");
        sql.append(getStatisticsBaseSql(filter, filter.getTransferType()));
        sql.append(" order by s.id desc");

        StringBuilder countSql = new StringBuilder();
        countSql.append("select count(s.id) ");
        countSql.append(getStatisticsBaseSql(filter, filter.getTransferType()));

        Query query = entityManager
                .createNativeQuery(sql.toString(), "StockProductQtyDetailListMapper")
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
            return new PageImpl<>(query.getResultList(), filter.getSortedPageable(), count);
        }
        return new PageImpl<>(new ArrayList<>(), filter.getSortedPageable(), count);
    }
}
