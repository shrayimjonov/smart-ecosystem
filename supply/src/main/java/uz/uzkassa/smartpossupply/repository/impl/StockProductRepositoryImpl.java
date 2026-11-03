package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.smartpossupply.domain.stock.StockProduct;
import uz.uzkassa.smartpossupply.dto.aggregationcodes.AggregationCodeStatisticsDTO;
import uz.uzkassa.smartpossupply.dto.aggregationcodes.AggregationCodeStatisticsItemDTO;
import uz.uzkassa.smartpossupply.dto.base.CommonNameDTO;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.order.terminal.TerminalTopProductsStatisticsDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductCapacityDTO;
import uz.uzkassa.smartpossupply.dto.stock.StockProductLookUpDTO;
import uz.uzkassa.smartpossupply.dto.stock.StockProductQtyDetailListDTO;
import uz.uzkassa.smartpossupply.dto.stock.StockProductQtyStatisticsDTO;
import uz.uzkassa.smartpossupply.dto.stockreport.StockProductStatisticsDTO;
import uz.uzkassa.smartpossupply.dto.stockreport.StockReportByProductDTO;
import uz.uzkassa.smartpossupply.dto.stockreport.StockReportByProductExtraInfoDTO;
import uz.uzkassa.smartpossupply.dto.stockreport.StockReportCompanyDTO;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.enums.StockStatus;
import uz.uzkassa.smartpossupply.enums.StockTransferStatus;
import uz.uzkassa.smartpossupply.enums.TransferType;
import uz.uzkassa.smartpossupply.filters.*;
import uz.uzkassa.smartpossupply.repository.StockProductRepositoryCustom;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StockProductRepositoryImpl extends BaseRepositoryImpl implements StockProductRepositoryCustom {

    @Override
    public List<StockProductLookUpDTO> getLookUp(StockInProductFilter filter) {
        boolean enabledSorting = filter.isEnabledSorting();
        filter.setCompanyId(getCompanyId(filter));
        filter.setBranchId(getBranchId(filter, false));
        filter.setWarehouseId(getWarehouseId(filter, false));

        StringBuilder sql = new StringBuilder();
        sql.append("select distinct p.id,");
        sql.append(" p.name,");

        sql.append(" wp.qty qtyInWarehouse, ");
        sql.append(" wp.reservation_qty as reservationQty, ");
        sql.append(" 0 as aggregationOrMarkQty, ");

        sql.append(" p.qty_in_package qtyInPackage, ");
        sql.append(" p.qty_in_box qtyInBox, ");
        sql.append(" p.qty_in_pallet qtyInPallet, ");

        sql.append(" p.purchase_price purchasePrice, ");
        sql.append(" p.price, ");

        sql.append(" p.excise_rate exciseRate, ");
        sql.append(" p.excise_sum exciseSum, ");

        sql.append(" p.vat vatCode, ");
        sql.append(" p.barcode barcode, ");
        sql.append(" p.vat_barcode vatBarcode, ");
        sql.append(" p.catalog_name catalogName, ");
        sql.append(" p.package_name packageName, ");
        sql.append(" p.package_code packageCode, ");

        sql.append(" p.capacity capacity, ");
        sql.append(" p.has_mark hasMark, ");

        sql.append(" p.product_type productTypeCode, ");
        sql.append(" p.block_products blockProducts, ");

        if (enabledSorting) {
            sql.append(" ps.sort_order sortOrder, ");
        } else {
            sql.append(" 0 sortOrder, ");
        }

        sql.append(" cc.id companyCommittentId, ");
        sql.append(" committent.id committentId, ");
        sql.append(" committent.name committentName, ");
        sql.append(" committent.tin committentTin, ");

        sql.append(" parent.id as parentId, ");
        sql.append(" parent.name as parentName, ");
        sql.append(" parent.barcode as parentBarcode, ");
        sql.append(" parent.vat_barcode as parentVatBarcode, ");

        sql.append(" p.box_barcode as boxBarcode, ");
        sql.append(" p.block_barcode as blockBarcode, ");

        sql.append(" p.exchange_rate as exchangeRate, ");
        sql.append(" p.currency_type as currencyType, ");
        sql.append(" p.foreign_price as foreignPrice ");

        sql.append(" from stock_product sp ");
        sql.append(" inner join stock s on s.id = sp.stock_id ");
        sql.append(" inner join product p on p.id = sp.product_id ");
        sql.append(" inner join warehouse_product wp on wp.product_id = p.id ");
        sql.append(" left join product parent on p.parent_id = parent.id ");
        sql.append(" left join company_committent cc on cc.id = p.company_committent_id ");
        sql.append(" left join company committent on committent.id = cc.committent_id ");
        if (enabledSorting) {
            sql.append(" left join product_sort ps on p.id = ps.product_id ");
            if (filter.getEnvironmentType() != null) {
                sql.append("and ps.environment_type = '").append(filter.getEnvironmentType()).append("'");
            }
            if (filter.getSortOrderType() != null) {
                sql.append(" and ps.sort_order_type = '").append(filter.getSortOrderType()).append("'");
            }
        }
        sql.append(" where sp.deleted = false and p.deleted = false ");
        sql.append(" and p.status='").append(CommonStatus.ACTIVE.getCode()).append("'");
        sql.append(" and s.transfer_type='").append(filter.getTransferType()).append("' ");
        sql.append(" and s.company_id = ").append(filter.getCompanyId());
        if (filter.getBranchId() != null) {
            sql.append(" and p.branch_id=").append(filter.getBranchId());
        }
        if (filter.getWarehouseId() != null) {
            sql.append(" and wp.warehouse_id=").append(filter.getWarehouseId());
        }
        if (filter.getCompanyCommittentId() != null) {
            sql.append(" and cc.id =").append(filter.getCompanyCommittentId());
        }
        if (filter.getCustomerId() != null && TransferType.OUT.name().equals(filter.getTransferType())) {
            sql.append(" and s.customer_id =").append(filter.getCustomerId());
        }
        if (filter.isNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(p.name)").append(filter.getSearchKey());
            sql.append(" or lower(p.barcode)").append(filter.getSearchKey());
            sql.append(" or lower(p.vat_barcode)").append(filter.getSearchKey());
            sql.append(" or lower(sp.name)").append(filter.getSearchKey());
            sql.append(") ");
        }
        if (!CollectionUtils.isEmpty(filter.getProductTypes())) {
            sql.append(" and (p.product_type is null or p.product_type not in (").append(filter.getProductTypes().stream().map(productType -> "'" + productType + "'").collect(Collectors.joining(","))).append(" ))");
        }
        if (enabledSorting) {
            sql.append(" order by ps.sort_order");
        } else {
            sql.append(" order by p.name");
        }

        Query query = entityManager
                .createNativeQuery(sql.toString(), "StockProductLookUpMapper")
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());
        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
        }
        return query.getResultList();
    }

    @Override
    public List<ProductCapacityDTO> lookUpSortedProducts(StockInProductFilter filter) {
        filter.setCompanyId(getCompanyId(filter));
        filter.setBranchId(getBranchId(filter, false));
        filter.setWarehouseId(getWarehouseId(filter, false));

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT p.id, p.name, p.capacity, ps.sort_order sortOrder ");
        sql.append(" FROM product p ");
        sql.append(" LEFT JOIN product_sort ps ON ps.product_id = p.id ");
        if (filter.getEnvironmentType() != null) {
            sql.append(" AND ps.environment_type = '").append(filter.getEnvironmentType()).append("'");
        }
        if (filter.getSortOrderType() != null) {
            sql.append(" AND ps.sort_order_type = '").append(filter.getSortOrderType()).append("'");
        }
        sql.append(" WHERE p.deleted = false ");
        sql.append(" AND p.status = '").append(CommonStatus.ACTIVE.getCode()).append("' ");
        if (filter.getBranchId() != null) {
            sql.append(" AND p.branch_id =").append(filter.getBranchId());
        }
        if (!CollectionUtils.isEmpty(filter.getProductTypes())) {
            sql.append(" AND (p.product_type IS NULL OR p.product_type NOT IN (").append(filter.getProductTypes().stream().map(productType -> "'" + productType + "'").collect(Collectors.joining(","))).append(" ))");
        }
        sql.append(" AND EXISTS (");
        sql.append(" SELECT 1 FROM stock_product sp ");
        sql.append(" JOIN stock s ON s.id = sp.stock_id ");
        sql.append(" WHERE sp.product_id = p.id ");
        sql.append(" AND sp.deleted = false ");
        sql.append(" AND s.transfer_type='").append(filter.getTransferType()).append("' ");
        sql.append(" AND s.company_id = ").append(filter.getCompanyId());
        if (filter.getCustomerId() != null && TransferType.OUT.name().equals(filter.getTransferType())) {
            sql.append(" AND s.customer_id =").append(filter.getCustomerId());
        }
        sql.append(" )");

        sql.append(" AND EXISTS (");
        sql.append(" SELECT 1 FROM warehouse_product wp ");
        sql.append(" WHERE wp.product_id = p.id ");
        if (filter.getWarehouseId() != null) {
            sql.append(" AND wp.warehouse_id =").append(filter.getWarehouseId());
        }
        sql.append(" )");

        if (filter.isNotEmpty()) {
            sql.append(" AND (");
            sql.append(" LOWER(p.name)").append(filter.getSearchKey());
            sql.append(" OR LOWER(p.barcode)").append(filter.getSearchKey());
            sql.append(" OR LOWER(p.vat_barcode)").append(filter.getSearchKey());
            sql.append(") ");
        }

        sql.append(" ORDER BY ps.sort_order NULLS LAST ");

        Query query = entityManager
                .createNativeQuery(sql.toString(), "ProductCapacityDTOMapper")
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
        }
        return query.getResultList();
    }

    @Override
    public List<ProductCapacityDTO> lookUpProducts(StockInProductFilter filter) {
        filter.setCompanyId(getCompanyId(filter));
        filter.setBranchId(getBranchId(filter, false));
        filter.setWarehouseId(getWarehouseId(filter, false));

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT p.id, p.name, p.capacity ");
        sql.append(" FROM product p ");
        sql.append(" WHERE p.deleted = false ");
        sql.append(" AND p.status = '").append(CommonStatus.ACTIVE.getCode()).append("' ");
        if (filter.getBranchId() != null) {
            sql.append(" AND p.branch_id =").append(filter.getBranchId());
        }
        if (!CollectionUtils.isEmpty(filter.getProductTypes())) {
            sql.append(" AND (p.product_type IS NULL OR p.product_type NOT IN (").append(filter.getProductTypes().stream().map(productType -> "'" + productType + "'").collect(Collectors.joining(","))).append(" ))");
        }

        sql.append(" AND EXISTS ( ");
        sql.append(" SELECT 1 FROM stock_product sp ");
        sql.append(" INNER JOIN stock s ON s.id = sp.stock_id ");
        sql.append(" WHERE sp.product_id = p.id ");
        sql.append(" AND sp.deleted = false ");
        sql.append(" AND s.transfer_type = '").append(filter.getTransferType()).append("' ");
        sql.append(" AND s.company_id = ").append(filter.getCompanyId());
        if (filter.getCustomerId() != null && TransferType.OUT.name().equals(filter.getTransferType())) {
            sql.append(" AND s.customer_id =").append(filter.getCustomerId());
        }
        sql.append(" )");

        sql.append(" AND EXISTS ( ");
        sql.append(" SELECT 1 FROM warehouse_product wp ");
        sql.append(" WHERE wp.product_id = p.id ");
        if (filter.getWarehouseId() != null) {
            sql.append(" AND wp.warehouse_id = ").append(filter.getWarehouseId());
        }
        sql.append(" )");

        if (filter.isNotEmpty()) {
            sql.append(" AND (");
            sql.append(" LOWER(p.name)").append(filter.getSearchKey());
            sql.append(" OR LOWER(p.barcode)").append(filter.getSearchKey());
            sql.append(" OR LOWER(p.vat_barcode)").append(filter.getSearchKey());
            sql.append(") ");
        }

        sql.append(" ORDER BY p.name");

        Query query = entityManager
                .createNativeQuery(sql.toString(), "ProductCapacityDTOMapper")
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
        }
        return query.getResultList();
    }

    @Override
    public StockProductLookUpDTO getProduct(Long productId, BaseFilter filter) {
        filter.setCompanyId(getCompanyId(filter));
        filter.setBranchId(getBranchId(filter, false));
        filter.setWarehouseId(getWarehouseId(filter, false));
        StringBuilder sql = new StringBuilder();
        sql.append("select distinct p.id, ");
        sql.append(" p.name, ");

        sql.append(" wp.qty qtyInWarehouse, ");
        sql.append(" wp.reservation_qty as reservationQty, ");
        sql.append(" 0 as aggregationOrMarkQty, ");

        sql.append(" p.qty_in_package qtyInPackage, ");
        sql.append(" p.qty_in_box qtyInBox, ");
        sql.append(" p.qty_in_pallet qtyInPallet, ");

        sql.append(" p.purchase_price purchasePrice, ");
        sql.append(" p.price, ");

        sql.append(" p.excise_rate exciseRate, ");
        sql.append(" p.excise_sum exciseSum, ");

        sql.append(" p.vat vatCode, ");
        sql.append(" p.barcode barcode, ");
        sql.append(" p.vat_barcode vatBarcode, ");
        sql.append(" p.catalog_name catalogName, ");
        sql.append(" p.package_name packageName, ");
        sql.append(" p.package_code packageCode, ");

        sql.append(" p.capacity capacity, ");
        sql.append(" p.has_mark hasMark, ");

        sql.append(" p.product_type productTypeCode, ");
        sql.append(" p.block_products blockProducts, ");

        sql.append(" cc.id companyCommittentId, ");
        sql.append(" committent.id committentId, ");
        sql.append(" committent.name committentName, ");
        sql.append(" committent.tin committentTin, ");

        sql.append(" parent.id as parentId, ");
        sql.append(" parent.name as parentName, ");
        sql.append(" parent.barcode as parentBarcode, ");
        sql.append(" parent.vat_barcode as parentVatBarcode, ");

        sql.append(" p.box_barcode as boxBarcode, ");
        sql.append(" p.block_barcode as blockBarcode, ");

        sql.append(" 0 as sortOrder, ");

        sql.append(" p.exchange_rate as exchangeRate, ");
        sql.append(" p.currency_type as currencyType, ");
        sql.append(" p.foreign_price as foreignPrice ");

        sql.append(" from stock_product sp ");
        sql.append(" inner join stock s on s.id = sp.stock_id ");
        sql.append(" inner join product p on p.id = sp.product_id ");
        sql.append(" inner join warehouse_product wp on wp.product_id = p.id ");
        sql.append(" left join product parent on p.parent_id = parent.id ");
        sql.append(" left join company_committent cc on cc.id = p.company_committent_id ");
        sql.append(" left join company committent on committent.id = cc.committent_id ");
        sql.append(" where sp.deleted = false and p.deleted = false ");
        sql.append(" and p.status = '").append(CommonStatus.ACTIVE.getCode()).append("'");
        sql.append(" and s.company_id = ").append(filter.getCompanyId());
        if (filter.getBranchId() != null) {
            sql.append(" and p.branch_id = ").append(filter.getBranchId());
        }
        if (filter.getWarehouseId() != null) {
            sql.append(" and wp.warehouse_id = ").append(filter.getWarehouseId());
        }
        sql.append(" and p.id = ").append(productId);

        Query query = entityManager.createNativeQuery(sql.toString(), "StockProductLookUpMapper");
        return (StockProductLookUpDTO) query.getSingleResult();
    }

    @Override
    public List<CommonNameDTO> lookUpStockProduct(StockInProductFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("select distinct sp.name");
        sql.append(" from stock_product sp ");
        sql.append(" inner join stock s on s.id = sp.stock_id ");
        sql.append(" inner join product p on p.id = sp.product_id ");
        sql.append(" where sp.deleted =false and p.deleted =false ");
        sql.append(" and sp.name is not null ");
        sql.append(" and p.id = ").append(filter.getProductId());
        sql.append(" and s.transfer_type='").append(filter.getTransferType()).append("' ");

        if (filter.isNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(p.name)").append(filter.getSearchKey());
            sql.append(" or lower(p.barcode)").append(filter.getSearchKey());
            sql.append(" or lower(p.vat_barcode)").append(filter.getSearchKey());
            sql.append(" or lower(sp.name)").append(filter.getSearchKey());
            sql.append(") ");
        }
        sql.append(" order by sp.name");

        Query query = entityManager.createNativeQuery(sql.toString(), "CommonNameDTOMapper");
        query.setFirstResult(filter.getStart()).setMaxResults(filter.getSize());
        return query.getResultList();
    }

    @Override
    public Page<StockProductQtyDetailListDTO> getQtyDetails(StockFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("select s.id as id, s.stock_number stockNumber, s.transfer_date as stockDate, s.completed_date as stockCompletedDate, sp.qty as qty, c.id as companyId, c.name as companyName, c.tin as companyTin ");
        sql.append(getBaseSql(filter));
        sql.append(" order by s.id desc");

        StringBuilder countSql = new StringBuilder();
        countSql.append("select count(s.id) ");
        countSql.append(getBaseSql(filter));

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

    private StringBuilder getBaseSql(StockFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append(" from stock_product sp ");
        sql.append(" inner join stock s on s.id = sp.stock_id ");
        sql.append(" inner join company c ");
        if (TransferType.OUT.equals(filter.getTransferType())) {
            sql.append(" on s.customer_id = c.id ");
        } else if (TransferType.IN.equals(filter.getTransferType())) {
            sql.append(" on s.supplier_id = c.id ");
        } else {
            sql.append(" on s.company_id = c.id ");
        }
        sql.append(" inner join product p on p.id = sp.product_id ");
        sql.append(" where sp.deleted =false and p.deleted =false ");
        sql.append(" and s.stock_transfer_id is null");
        sql.append(" and p.id = ").append(filter.getProductId());
        if (filter.getProductSerialId() != null) {
            sql.append(" and sp.product_serial_id = ").append(filter.getProductSerialId());
        }
        if (filter.getProductContractId() != null) {
            sql.append(" and sp.product_contract_id = ").append(filter.getProductContractId());
        }
        sql.append(" and s.transfer_type='").append(filter.getTransferType()).append("' ");
        sql.append(" and s.status='").append(StockStatus.COMPLETED.getCode()).append("'");
        sql.append(" and (s.warehouse_id=").append(filter.getWarehouseId());
        sql.append(" or sp.warehouse_id=").append(filter.getWarehouseId());
        sql.append(" )");
        if (filter.getFrom() != null) {
            sql.append(" and s.transfer_date>=:fromDate ");
        }
        if (filter.getTo() != null) {
            sql.append(" and s.transfer_date<=:toDate ");
        }
        return sql;
    }

    @Override
    public StockProductQtyStatisticsDTO getProductQtyStatistics(StockFilter filter) {
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
        // stock-adjustment
        sql.append(" select coalesce(sum(sp.qty),0) as qty, 'STOCK_ADJUSTMENT_TOTAL' as dataType");
        sql.append(" from stock_adjustment_product sp ");
        sql.append(" inner join stock_adjustment s on s.id = sp.stock_adjustment_id ");
        sql.append(" inner join product p on sp.product_id = p.id ");
        sql.append(" where sp.deleted =false and p.deleted =false ");
        sql.append(" and p.id = ").append(filter.getProductId());
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
        sql.append(" and tp.id = ").append(filter.getProductId());
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
        sql.append(" and fp.id = ").append(filter.getProductId());
        sql.append(" and s.status='").append(StockTransferStatus.APPROVED.getCode()).append("'");
        sql.append(" and fw.id=").append(filter.getWarehouseId());
        if (filter.getFrom() != null) {
            sql.append(" and s.transfer_date>=:fromDate ");
        }
        if (filter.getTo() != null) {
            sql.append(" and s.transfer_date<=:toDate ");
        }
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

    @Override
    public AggregationCodeStatisticsDTO getAggregationCodeStatistics(AggregationCodesReportFilter filter) {
        filter.setWarehouseId(getWarehouseId(filter));
        StringBuilder sql = new StringBuilder();
        sql.append("""
                select count(spa.id) as total, 'AGGREGATION' as codeType
                from stock_product_aggregation spa
                         join stock_product sp on spa.stock_product_id = sp.id
                         join stock s on sp.stock_id = s.id
                where spa.deleted = false
                  and s.deleted = false
                  and spa.used = false
                  and s.status = 'COMPLETED'
                  and s.transfer_type = 'IN'
                """);
        sql.append("and spa.product_id = ").append(filter.getProductId()).append("\n");
        sql.append("and s.warehouse_id = ").append(filter.getWarehouseId()).append("\n");
        sql.append("union all").append("\n");
        sql.append("""
                select count(spb.id) as total, 'AGGREGATION_BLOCK' as codeType
                from stock_product_aggregation_block spb
                         join stock_product sp on spb.stock_product_id = sp.id
                         join stock s on sp.stock_id = s.id
                where spb.deleted = false
                  and s.deleted = false
                  and spb.used = false
                  and s.status = 'COMPLETED'
                  and s.transfer_type = 'IN'
                """);
        sql.append("and spb.product_id = ").append(filter.getProductId()).append("\n");
        sql.append("and s.warehouse_id = ").append(filter.getWarehouseId()).append("\n");
        sql.append("union all").append("\n");
        sql.append("""
                select count(spm.id) as total, 'MARK' as codeType
                from stock_product_mark spm
                         join stock_product sp on spm.stock_product_id = sp.id
                         join stock s on sp.stock_id = s.id
                where spm.deleted = false
                  and s.deleted = false
                  and spm.used = false
                  and s.status = 'COMPLETED'
                  and s.transfer_type = 'IN'
                """);
        sql.append("and spm.product_id = ").append(filter.getProductId()).append("\n");
        sql.append("and s.warehouse_id = ").append(filter.getWarehouseId());

        Query query = entityManager.createNativeQuery(sql.toString(), AggregationCodeStatisticsItemDTO.class);
        List<AggregationCodeStatisticsItemDTO> statistics = query.getResultList();

        AggregationCodeStatisticsDTO result = new AggregationCodeStatisticsDTO();
        statistics.forEach(aggregationCodeStatisticsItemDTO -> {
            switch (aggregationCodeStatisticsItemDTO.getCodeType()) {
                case "AGGREGATION" ->
                        result.setStockProductAggregationTotal(aggregationCodeStatisticsItemDTO.getTotal());
                case "AGGREGATION_BLOCK" ->
                        result.setStockProductAggregationBlockTotal(aggregationCodeStatisticsItemDTO.getTotal());
                case "MARK" -> result.setStockProductMarkTotal(aggregationCodeStatisticsItemDTO.getTotal());
            }
        });
        return result;
    }

    private StringBuilder getStatisticsBaseSql(StockFilter filter, TransferType transferType) {
        StringBuilder sql = new StringBuilder();
        sql.append(" from stock_product sp ");
        sql.append(" inner join product p on p.id = sp.product_id ");
        sql.append(" inner join stock s on s.id = sp.stock_id ");
        sql.append(" where sp.deleted =false and p.deleted =false ");
        sql.append(" and s.stock_transfer_id is null");
        sql.append(" and p.id = ").append(filter.getProductId());
        sql.append(" and s.status='").append(StockStatus.COMPLETED.getCode()).append("'");
        sql.append(" and (s.warehouse_id=").append(filter.getWarehouseId());
        sql.append(" or sp.warehouse_id=").append(filter.getWarehouseId());
        sql.append(" )");
        sql.append(" and s.transfer_type='").append(transferType.name()).append("'");
        if (filter.getFrom() != null) {
            sql.append(" and s.transfer_date>=:fromDate ");
        }
        if (filter.getTo() != null) {
            sql.append(" and s.transfer_date<=:toDate ");
        }
        return sql;
    }

    public boolean checkProductInStock(Long productId) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select count(s.id)");
        sql.append(" from stock_product sp ");
        sql.append(" inner join stock s on s.id = sp.stock_id ");
        sql.append(" inner join product p on p.id = sp.product_id ");
        sql.append(" where sp.deleted =false and p.deleted =false ");
        sql.append(" and p.id = ").append(productId);
        sql.append(" and s.status !='").append(StockStatus.DRAFT.getCode()).append("'");
        long count = (long) entityManager.createNativeQuery(sql.toString()).getSingleResult();
        return count > 0;
    }

    @Override
    public ResultList<StockProduct> getList(ReconciliationActFilter filter) {
        ResultList<StockProduct> resultList = new ResultList<>();
        StringBuilder jpql = new StringBuilder();
        jpql.append("""
                SELECT sp FROM StockProduct sp
                    JOIN FETCH sp.stock s
                    JOIN FETCH sp.product
                WHERE sp.deleted = false AND s.status = COMPLETED""");

        StringBuilder countSQL = new StringBuilder();
        countSQL.append("""
                SELECT count(sp.id) FROM StockProduct sp
                    JOIN sp.stock s
                WHERE sp.deleted = false""");

        if (filter.getCompanyId() != null) {
            jpql.append(" AND s.companyId = :companyId");
            countSQL.append(" AND s.companyId = :companyId");
        }
        if (filter.getCustomerId() != null) {
            jpql.append(" AND s.customerId = :customerId");
            countSQL.append(" AND s.customerId = :customerId");
        }
        if (filter.getCustomerBranchId() != null) {
            jpql.append(" AND s.deliveryBranchId = :customerBranchId");
            countSQL.append(" AND s.deliveryBranchId = :customerBranchId");
        }
        if (filter.getFrom() != null) {
            jpql.append(" AND sp.createdDate >= :fromDate ");
            countSQL.append(" AND sp.createdDate >= :fromDate");
        }
        if (filter.getTo() != null) {
            jpql.append(" AND sp.createdDate <= :toDate ");
            countSQL.append(" AND sp.createdDate <= :toDate");
        }
        if (filter.getStockOutId() != null) {
            jpql.append(" AND sp.stockId = :stockOutId");
            countSQL.append(" AND sp.stockId = :stockOutId");
        }

        jpql.append(" order by sp.").append(filter.getOrderBy());
        jpql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<StockProduct> query = entityManager.createQuery(jpql.toString(), StockProduct.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSQL.toString(), Long.class);

        if (filter.getCompanyId() != null) {
            query.setParameter("companyId", filter.getCompanyId());
            countQuery.setParameter("companyId", filter.getCompanyId());
        }
        if (filter.getCustomerId() != null) {
            query.setParameter("customerId", filter.getCustomerId());
            countQuery.setParameter("customerId", filter.getCustomerId());
        }
        if (filter.getCustomerBranchId() != null) {
            query.setParameter("customerBranchId", filter.getCustomerBranchId());
            countQuery.setParameter("customerBranchId", filter.getCustomerBranchId());
        }
        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFrom());
            countQuery.setParameter("fromDate", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getTo());
            countQuery.setParameter("toDate", filter.getTo());
        }

        if (filter.getStockOutId() != null) {
            query.setParameter("stockOutId", filter.getStockOutId());
            countQuery.setParameter("stockOutId", filter.getStockOutId());
        }

        Long count = countQuery.getSingleResult();
        if (count > 0) {
            resultList.setList(query.getResultList());
            resultList.setCount(count);
        }

        return resultList;
    }

    @Override
    public ResultList<StockReportByProductDTO> getStockReport(StockReportByProductFilter filter) {
        ResultList<StockReportByProductDTO> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder("""
                SELECT p.id          AS productId,
                       p.name        AS productName,
                       p.barcode     AS  barcode,
                       sum(sp.total_brutto)  AS totalBrutto,
                       sum(sp.total_netto)   AS totalNetto,
                       sum(sp.qty)           AS totalQty,
                       sum(sp.qty_in_box)           AS totalQtyInBox,
                       sum(sp.total_selling_price) AS totalSellingPrice,
                       sum(sp.total_purchase_price) AS  totalPurchasePrice,
                       count(DISTINCT s.customer_id)  AS totalCustomerCount,
                       array_agg(DISTINCT r.name_ru) AS regions
                FROM stock_product sp
                         join product p on sp.product_id = p.id
                         join stock s on sp.stock_id = s.id
                         left join customer_branch cb on s.delivery_branch_id = cb.id
                         left join region r on cb.region_id = r.id
                WHERE s.company_id = :companyId
                AND sp.created_date BETWEEN :fromDate AND :toDate
                AND s.status = 'COMPLETED'
                AND s.deleted = false
                """);

        StringBuilder countSQL = new StringBuilder("""
                SELECT count(*)
                FROM (
                    SELECT sp.product_id
                    FROM stock_product sp
                             join stock s on sp.stock_id = s.id
                """);
        if (!filter.getCategoryIds().isEmpty()) {
            countSQL.append("  JOIN product p ON sp.product_id = p.id");
        }
        countSQL.append(" WHERE s.company_id = :companyId AND sp.created_date BETWEEN :fromDate AND :toDate AND s.status = 'COMPLETED' AND s.deleted = false");

        if (filter.getBranchId() != null) {
            sql.append(" AND s.branch_id = :branchId ");
            countSQL.append(" AND s.branch_id = :branchId ");
        }
        if (filter.getTransferType() != null) {
            sql.append(" AND s.transfer_type = :transferType ");
            countSQL.append(" AND s.transfer_type = :transferType ");
        }
        if (!filter.getCategoryIds().isEmpty()) {
            sql.append(" AND p.category_id in (:categoryIds)");
            countSQL.append(" AND p.category_id in (:categoryIds)");
        }
        if (!filter.getProductIds().isEmpty()) {
            sql.append(" AND sp.product_id in (:productIds)");
            countSQL.append(" AND sp.product_id in (:productIds)");
        }

        sql.append(" GROUP BY p.id");
        countSQL.append(" GROUP BY sp.product_id ) grouped_products");
        Query query = entityManager.createNativeQuery(sql.toString(), StockReportByProductDTO.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());
        query.setParameter("companyId", filter.getCompanyId());
        query.setParameter("fromDate", filter.getFrom());
        query.setParameter("toDate", filter.getTo());

        Query countQuery = entityManager.createNativeQuery(countSQL.toString(), Long.class);
        countQuery.setParameter("companyId", filter.getCompanyId());
        countQuery.setParameter("fromDate", filter.getFrom());
        countQuery.setParameter("toDate", filter.getTo());

        if (filter.getBranchId() != null) {
            query.setParameter("branchId", filter.getBranchId());
            countQuery.setParameter("branchId", filter.getBranchId());
        }
        if (filter.getTransferType() != null) {
            query.setParameter("transferType", filter.getTransferType());
            countQuery.setParameter("transferType", filter.getTransferType());
        }
        if (!filter.getCategoryIds().isEmpty()) {
            query.setParameter("categoryIds", filter.getCategoryIds());
            countQuery.setParameter("categoryIds", filter.getCategoryIds());
        }
        if (!filter.getProductIds().isEmpty()) {
            query.setParameter("productIds", filter.getProductIds());
            countQuery.setParameter("productIds", filter.getProductIds());
        }

        long count = ((Number) countQuery.getSingleResult()).longValue();
        if (count > 0) {
            List<StockReportByProductDTO> list = query.getResultList();
            resultList.setList(list);
            resultList.setCount(count);
        }
        return resultList;
    }

    @SuppressWarnings("unchecked")
    public ResultList<StockReportByProductExtraInfoDTO> getStockReportExtraInfo(StockReportByProductFilter filter) {
        ResultList<StockReportByProductExtraInfoDTO> resultList = new ResultList<>();

        StringBuilder sql = new StringBuilder("""
                SELECT sp.product_id as productId,
                       p.name        as productName,
                       -- totalQty: base sum minus selected extras
                       sum(sp.qty)
                         - (CASE WHEN :hasDiscount = true THEN sum(CASE WHEN sp.discount_id IS NOT NULL THEN sp.qty ELSE 0 END) ELSE 0 END)
                         - (CASE WHEN :hasPriceList = true THEN sum(CASE WHEN s.price_list_id IS NOT NULL THEN sp.qty ELSE 0 END) ELSE 0 END)
                         as totalQty,
                       -- always return discount and price list qty
                       sum(CASE WHEN sp.discount_id IS NOT NULL THEN sp.qty ELSE 0 END) as totalQtyWithDiscount,
                       sum(CASE WHEN s.price_list_id IS NOT NULL THEN sp.qty ELSE 0 END) as totalQtyWithPriceList,
                       sum(sp.total_selling_price) as totalSellingPrice,
                       sum(sp.total_purchase_price) AS  totalPurchasePrice,
                       count(s.customer_id)  as totalCustomerCount
                FROM stock_product sp
                         join product p on sp.product_id = p.id
                         join stock s on sp.stock_id = s.id
                         left join customer_branch cb on s.delivery_branch_id = cb.id
                WHERE s.company_id = :companyId
                  AND sp.created_date BETWEEN :fromDate AND :toDate
                  AND s.status = 'COMPLETED'
                  AND s.deleted = false
                """);

        StringBuilder countSQL = new StringBuilder("""
                 SELECT count(sp.id)
                 FROM stock_product sp
                          join stock s on sp.stock_id = s.id
                """);

        if (!filter.getCategoryIds().isEmpty()) {
            countSQL.append(" JOIN product p ON sp.product_id = p.id");
        }
        countSQL.append(" WHERE s.company_id = :companyId AND sp.created_date BETWEEN :fromDate AND :toDate AND s.status = 'COMPLETED' AND s.deleted = false");

        if (filter.getBranchId() != null) {
            sql.append(" AND s.branch_id = :branchId ");
            countSQL.append(" AND s.branch_id = :branchId ");
        }
        if (filter.getTransferType() != null) {
            sql.append(" AND s.transfer_type = :transferType ");
            countSQL.append(" AND s.transfer_type = :transferType ");
        }
        if (!filter.getCategoryIds().isEmpty()) {
            sql.append(" AND p.category_id in (:categoryIds)");
            countSQL.append(" AND p.category_id in (:categoryIds)");
        }
        if (!filter.getProductIds().isEmpty()) {
            sql.append(" AND sp.product_id in (:productIds)");
            countSQL.append(" AND sp.product_id in (:productIds)");
        }

        sql.append(" GROUP BY p.name, sp.product_id");

        Query query = entityManager.createNativeQuery(sql.toString(), StockReportByProductExtraInfoDTO.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());
        query.setParameter("companyId", filter.getCompanyId());
        query.setParameter("fromDate", filter.getFrom());
        query.setParameter("toDate", filter.getTo());
        query.setParameter("hasDiscount", filter.isHasDiscount());
        query.setParameter("hasPriceList", filter.isHasPriceList());

        Query countQuery = entityManager.createNativeQuery(countSQL.toString(), Long.class);
        countQuery.setParameter("companyId", filter.getCompanyId());
        countQuery.setParameter("fromDate", filter.getFrom());
        countQuery.setParameter("toDate", filter.getTo());

        if (filter.getBranchId() != null) {
            query.setParameter("branchId", filter.getBranchId());
            countQuery.setParameter("branchId", filter.getBranchId());
        }
        if (filter.getTransferType() != null) {
            query.setParameter("transferType", filter.getTransferType());
            countQuery.setParameter("transferType", filter.getTransferType());
        }
        if (!filter.getCategoryIds().isEmpty()) {
            query.setParameter("categoryIds", filter.getCategoryIds());
            countQuery.setParameter("categoryIds", filter.getCategoryIds());
        }
        if (!filter.getProductIds().isEmpty()) {
            query.setParameter("productIds", filter.getProductIds());
            countQuery.setParameter("productIds", filter.getProductIds());
        }

        long count = ((Number) countQuery.getSingleResult()).longValue();
        if (count > 0) {
            resultList.setList(query.getResultList());
            resultList.setCount(count);
        }
        return resultList;
    }

    @Override
    public List<StockReportCompanyDTO> getStockReportCompanyInfo(StockReportByProductFilter filter) {
        StringBuilder sql = new StringBuilder("""
                SELECT s.customer_id AS customerId,
                       c.name        AS customerName,
                       SUM(sp.qty)   AS totalQty
                FROM stock_product sp
                         JOIN stock s on sp.stock_id = s.id
                         JOIN company c on s.customer_id = c.id
                WHERE s.company_id = :companyId
                    AND sp.created_date BETWEEN :fromDate AND :toDate
                    AND s.status = 'COMPLETED'
                    AND sp.product_id = :productId
                """);

        if (filter.getBranchId() != null) {
            sql.append(" AND s.branch_id = :branchId ");
        }
        if (filter.getTransferType() != null) {
            sql.append(" AND s.transfer_type = :transferType ");
        }

        sql.append(" GROUP BY s.customer_id, c.name");

        Query query = entityManager.createNativeQuery(sql.toString(), StockReportCompanyDTO.class);

        query.setParameter("companyId", filter.getCompanyId());
        query.setParameter("productId", filter.getProductId());
        query.setParameter("fromDate", filter.getFrom());
        query.setParameter("toDate", filter.getTo());

        if (filter.getBranchId() != null) {
            query.setParameter("branchId", filter.getBranchId());
        }
        if (filter.getTransferType() != null) {
            query.setParameter("transferType", filter.getTransferType());
        }

        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public ResultList<TerminalTopProductsStatisticsDTO> getTopProducts(StockFilter filter) {
        StringBuilder withQuery = new StringBuilder("""
                WITH stock_product_result AS (
                    SELECT distinct sp.product_id           AS productId,
                           COALESCE(SUM(sp.total_brutto),0) AS totalBrutto,
                           COALESCE(SUM(sp.total_netto),0)  AS totalNetto,
                           COALESCE(SUM(sp.qty),0)          AS qty,
                           COALESCE(SUM(sp.qty_in_box),0)   AS qtyInBox,
                           COALESCE(SUM(sp.total_selling_price),0)   AS total_selling_price
                    FROM stock_product sp
                            JOIN stock s ON sp.stock_id = s.id
                            JOIN orders o ON s.order_id = o.id
                    WHERE s.company_id = :companyId
                      AND s.transfer_type = 'OUT'
                      AND o.expeditor_id = :expeditorId
                """);

        if (filter.getFrom() != null) {
            withQuery.append(" AND s.created_date >= :fromDate");
        }

        if (filter.getTo() != null) {
            withQuery.append(" AND s.created_date <= :toDate");
        }

        withQuery.append(" GROUP BY sp.product_id)");

        StringBuilder sql = new StringBuilder(withQuery);

        sql.append("""
                SELECT spr.*,
                       p.name as productName
                FROM stock_product_result spr
                         JOIN product p ON spr.productId = p.id
                ORDER BY spr.qty DESC;
                """);

        String countSQL = withQuery + """
                SELECT COUNT(*) FROM stock_product_result
                """;

        Query nativeQuery = entityManager.createNativeQuery(sql.toString(), TerminalTopProductsStatisticsDTO.class);
        Query countQuery = entityManager.createNativeQuery(countSQL, Long.class);
        nativeQuery.setParameter("companyId", filter.getCompanyId());
        countQuery.setParameter("companyId", filter.getCompanyId());
        nativeQuery.setParameter("expeditorId", filter.getUserId());
        countQuery.setParameter("expeditorId", filter.getUserId());
        if (filter.getFrom() != null) {
            nativeQuery.setParameter("fromDate", filter.getFrom());
            countQuery.setParameter("fromDate", filter.getFrom());
        }
        if (filter.getTo() != null) {
            nativeQuery.setParameter("toDate", filter.getTo());
            countQuery.setParameter("toDate", filter.getTo());
        }
        ResultList<TerminalTopProductsStatisticsDTO> resultList = new ResultList<>();
        Long count = (Long) countQuery.getSingleResult();
        if (count > 0) {
            resultList.setList(nativeQuery.getResultList());
            resultList.setCount(count);
        }
        return resultList;
    }
}
