package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.util.StringUtils;
import uz.uzkassa.smartpossupply.domain.stock.Stock;
import uz.uzkassa.smartpossupply.domain.warehouse.WarehouseProduct;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.stock.CommonStockStatisticsDTO;
import uz.uzkassa.smartpossupply.dto.stockreport.StockReportDTO;
import uz.uzkassa.smartpossupply.enums.RelationType;
import uz.uzkassa.smartpossupply.enums.StockStatus;
import uz.uzkassa.smartpossupply.enums.TransferType;
import uz.uzkassa.smartpossupply.filters.ProductFilter;
import uz.uzkassa.smartpossupply.filters.StockFilter;
import uz.uzkassa.smartpossupply.repository.StockInRepositoryCustom;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.util.List;

@SuppressWarnings("unused")
public class StockInRepositoryImpl
    extends BaseFilterableRepositoryImpl<Stock, StockFilter>
    implements StockInRepositoryCustom {

    @Override
    protected Class<Stock> getEntityClass() {
        return Stock.class;
    }

    @Override
    protected String getEntityAlias() {
        return "s";
    }

    @Override
    public void applyFilter(StockFilter filter) {
        filter.setCompanyId(getCompanyId(filter));
        filter.setBranchId(getBranchId(filter, false));
        filter.setWarehouseId(getWarehouseId(filter, false));
    }

    @Override
    public String getBaseQuery(StockFilter filter, boolean includeFetch) {
        boolean advancedSearch = getAdvanceSearch(filter);
        StringBuilder sql = new StringBuilder(" FROM Stock s ");

        // Add base joins
        if (includeFetch) {
            sql.append("JOIN FETCH s.supplier supp ")
                .append("JOIN FETCH s.customer cus ")
                .append("JOIN FETCH s.company c ")
                .append("JOIN FETCH s.branch b ")
                .append("LEFT JOIN FETCH s.warehouse w ")
                .append("LEFT JOIN FETCH s.stockTransfer st ")
                .append("LEFT JOIN FETCH s.oldStock os ")
                .append("LEFT JOIN FETCH s.factura f ")
                .append("LEFT JOIN FETCH s.acceptanceTransferAct act ")
                .append("LEFT JOIN FETCH s.aic aic ")
                .append("LEFT JOIN FETCH s.party party ");
        } else {
            sql.append("JOIN s.supplier supp ")
                .append("JOIN s.customer cus ")
                .append("JOIN s.company c ")
                .append("JOIN s.branch b ")
                .append("LEFT JOIN s.warehouse w ")
                .append("LEFT JOIN s.stockTransfer st ")
                .append("LEFT JOIN s.oldStock os ")
                .append("LEFT JOIN s.factura f ")
                .append("LEFT JOIN s.acceptanceTransferAct act ")
                .append("LEFT JOIN s.aic aic ")
                .append("LEFT JOIN s.party party ");
        }

        // Advanced search joins (never use FETCH for these)
        if (advancedSearch) {
            sql.append("JOIN s.products sp ");
            if (StringUtils.hasText(filter.getAggregationCode())) {
                sql.append("JOIN sp.stockProductAggregations spa ");
            } else if (StringUtils.hasText(filter.getMarkCode())) {
                sql.append("JOIN sp.stockProductMarks spm ");
            } else if (StringUtils.hasText(filter.getAggregationBlockCode())) {
                sql.append("JOIN sp.stockProductAggregationBlocks spt ");
            } else if (StringUtils.hasText(filter.getBarcode()) || StringUtils.hasText(filter.getVatBarcode())) {
                sql.append("JOIN sp.product p ");
            }
        }

        // Base condition - always IN type for this repository
        sql.append("WHERE s.transferType = :transferType ");

        // Deleted flag handling
        if (!filter.isFetchDeleted()) {
            sql.append("AND s.deleted = false ");
        }

        // Dynamic conditions
        appendCondition(sql, filter.getCompanyId() != null, "AND c.id = :companyId ");
        appendCondition(sql, filter.getCreatorId() != null, "AND s.creatorId = :creatorId ");
        appendCondition(sql, filter.getSupplierId() != null, "AND supp.id = :supplierId ");
        appendCondition(sql, filter.getCustomerId() != null, "AND cus.id = :customerId ");
        appendCondition(sql, filter.getBranchId() != null, "AND b.id = :branchId ");
        appendCondition(sql, filter.getWarehouseId() != null, "AND w.id = :warehouseId ");
        appendCondition(sql, filter.getCashierId() != null, "AND s.cashierId = :cashierId ");
        appendCondition(sql, filter.getStatus() != null, "AND s.status = :status ");
        appendCondition(sql, filter.getFrom() != null, "AND s.transferDate >= :fromDate ");
        appendCondition(sql, filter.getTo() != null, "AND s.transferDate <= :toDate ");

        // Boolean condition for hasFactura
        if (filter.getHasFactura() != null) {
            sql.append(filter.getHasFactura() ?
                "AND s.facturaId IS NOT NULL " :
                "AND s.facturaId IS NULL ");
        }

        // RelationType condition
        if (filter.getRelationType() != null) {
            switch (RelationType.valueOf(filter.getRelationType())) {
                case ACT:
                    sql.append("AND s.acceptanceTransferActId IS NOT NULL ");
                    break;
                case FACTURA:
                    sql.append("AND s.facturaId IS NOT NULL ");
                    break;
                case AGGREGATION:
                    sql.append("AND s.partyId IS NOT NULL ");
                    break;
                case AIC:
                    sql.append("AND s.aicId IS NOT NULL ");
                    break;
                default:
                    sql.append("AND s.aicId IS NULL AND s.partyId IS NULL AND s.facturaId IS NULL AND s.acceptanceTransferActId IS NULL ");
            }
        }

        // Advanced search conditions
        if (advancedSearch) {
            sql.append("AND sp.deleted = false ");

            if (StringUtils.hasText(filter.getAggregationCode())) {
                sql.append("AND spa.code = :code ");
            } else if (StringUtils.hasText(filter.getMarkCode())) {
                sql.append("AND spm.code = :code ");
            } else if (StringUtils.hasText(filter.getAggregationBlockCode())) {
                sql.append("AND spt.code = :code ");
            } else if (StringUtils.hasText(filter.getBarcode())) {
                sql.append("AND p.barcode = :barcode ");
            } else if (StringUtils.hasText(filter.getVatBarcode())) {
                sql.append("AND p.vatBarcode = :vatBarcode ");
            } else if (StringUtils.hasText(filter.getAcceptanceTransferActNumber())) {
                sql.append("AND act.actNo LIKE :acceptanceTransferActNumber ");
            } else if (StringUtils.hasText(filter.getReceiptUid())) {
                sql.append("AND s.receiptUid LIKE :receiptUid ");
            } else if (StringUtils.hasText(filter.getFactura())) {
                sql.append("AND (f.facturaId LIKE :factura OR f.facturaNo LIKE :factura) ");
            }
        } else if (filter.isNotEmpty()) {
            // Standard search
            sql.append("AND (")
                .append("LOWER(s.stockNumber)").append(filter.getSearchKey())
                .append(" OR LOWER(supp.name)").append(filter.getSearchKey())
                .append(" OR LOWER(supp.tin)").append(filter.getSearchKey())
                .append(" OR LOWER(cus.name)").append(filter.getSearchKey())
                .append(" OR LOWER(cus.tin)").append(filter.getSearchKey())
                .append(") ");
        }

        return sql.toString();
    }

    @Override
    public void setParameters(TypedQuery<?> query, StockFilter filter) {
        // Required parameter - always IN for this repository
        query.setParameter("transferType", TransferType.IN);

        // Optional parameters
        setParameterIfNotNull(query, "companyId", filter.getCompanyId());
        setParameterIfNotNull(query, "creatorId", filter.getCreatorId());
        setParameterIfNotNull(query, "supplierId", filter.getSupplierId());
        setParameterIfNotNull(query, "customerId", filter.getCustomerId());
        setParameterIfNotNull(query, "branchId", filter.getBranchId());
        setParameterIfNotNull(query, "warehouseId", filter.getWarehouseId());
        setParameterIfNotNull(query, "planRouteId", filter.getPlanRouteId());
        setParameterIfNotNull(query, "cashierId", filter.getCashierId());

        if (filter.getStatus() != null) {
            query.setParameter("status", StockStatus.valueOf(filter.getStatus()));
        }

        setParameterIfNotNull(query, "fromDate", filter.getFrom());
        setParameterIfNotNull(query, "toDate", filter.getTo());

        // Advanced search parameters (mutually exclusive)
        if (getAdvanceSearch(filter)) {
            if (StringUtils.hasText(filter.getAggregationCode())) {
                query.setParameter("code", filter.getAggregationCode());
            } else if (StringUtils.hasText(filter.getMarkCode())) {
                query.setParameter("code", filter.getMarkCode());
            } else if (StringUtils.hasText(filter.getAggregationBlockCode())) {
                query.setParameter("code", filter.getAggregationBlockCode());
            } else if (StringUtils.hasText(filter.getBarcode())) {
                query.setParameter("barcode", filter.getBarcode());
            } else if (StringUtils.hasText(filter.getVatBarcode())) {
                query.setParameter("vatBarcode", filter.getVatBarcode());
            } else if (StringUtils.hasText(filter.getAcceptanceTransferActNumber())) {
                query.setParameter("acceptanceTransferActNumber", filter.getAcceptanceTransferActNumber());
            } else if (StringUtils.hasText(filter.getReceiptUid())) {
                query.setParameter("receiptUid", filter.getReceiptUid());
            } else if (StringUtils.hasText(filter.getFactura())) {
                query.setParameter("factura", filter.getFactura());
            }
        } else if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
        }
    }

    @Override
    public ResultList<WarehouseProduct> getBalanceReport(ProductFilter filter) {

        filter.setCompanyId(getCompanyId(filter));
        filter.setBranchId(getBranchId(filter, false));
        filter.setWarehouseId(getWarehouseId(filter, false));

        ResultList<WarehouseProduct> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select wp ");
        sql.append(getBalanceReportBaseSql(filter));
        String countSql = sql.toString().replace("select wp ", "select count(wp.id)");

        sql.append(" order by wp.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<WarehouseProduct> query = entityManager
                .createQuery(sql.toString(), WarehouseProduct.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        query.setParameter("companyId", filter.getCompanyId());
        countQuery.setParameter("companyId", filter.getCompanyId());

        if (filter.getBranchId() != null) {
            query.setParameter("branchId", filter.getBranchId());
            countQuery.setParameter("branchId", filter.getBranchId());
        }
        if (filter.getWarehouseId() != null) {
            query.setParameter("warehouseId", filter.getWarehouseId());
            countQuery.setParameter("warehouseId", filter.getWarehouseId());
        }
        if (filter.getCategoryId() != null) {
            query.setParameter("categoryId", filter.getCategoryId());
            countQuery.setParameter("categoryId", filter.getCategoryId());
        }
        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
            countQuery.setParameter("searchKey", filter.getSearchValue());
        }
        Long count = countQuery.getSingleResult();
        if (count > 0) {
            resultList.setCount(count);
            resultList.setList(query.getResultList());
        }
        return resultList;
    }

    private StringBuilder getBalanceReportBaseSql(ProductFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append(" from WarehouseProduct wp");
        sql.append(" join wp.product p");
        sql.append(" join wp.warehouse w");
        sql.append(" where p.companyId =:companyId");

        if (filter.isHideZero()) {
            sql.append(" and wp.qty>0");
        }
        if (filter.getBranchId() != null) {
            sql.append(" and p.branchId =:branchId");
        }
        if (filter.getWarehouseId() != null) {
            sql.append(" and w.id=:warehouseId");
        }
        if (filter.getCategoryId() != null) {
            sql.append(" and p.categoryId=:categoryId");
        }
        if (filter.isNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(p.name)").append(filter.getSearchKey());
            sql.append(" or lower(p.barcode)").append(filter.getSearchKey());
            sql.append(" or lower(p.vatBarcode)").append(filter.getSearchKey());
            sql.append(" or lower(p.catalogName)").append(filter.getSearchKey());
            sql.append(") ");
        }
        return sql;
    }

    public Page<StockReportDTO> getReport(ProductFilter filter) {

        filter.setCompanyId(getCompanyId(filter));
        filter.setBranchId(getBranchId(filter, false));
        filter.setWarehouseId(getWarehouseId(filter, false));

        StringBuilder sql = new StringBuilder();
        StringBuilder countSql = new StringBuilder();

        sql.append(" with stock_in as ( ");
        sql.append(" select p.id, p.name,p.barcode, sum(sp.qty) as qty, avg(selling_price) as sellingPrice, ");
        sql.append(" avg(total_selling_price) as totalSellingPrice, avg(purchase_price) as purchasePrice, ");
        sql.append(" avg(total_purchase_price) as totalPurchasePrice, avg(sp.markup_percent) as markupPercent ");
        sql.append(" from stock_product sp ");
        sql.append(" inner join stock s on s.id = sp.stock_id ");
        sql.append(" inner join product p on sp.product_id = p.id ");
        if (filter.getCategoryId() != null) {
            sql.append(" inner join category c on p.category_id = c.id ");
        }
        sql.append(" where s.company_id = ").append(SecurityUtils.getCurrentCompanyId());
        if (filter.getCategoryId() != null) {
            sql.append(" and c.deleted = false and c.id = ").append(filter.getCategoryId());
        }
        sql.append(" and s.deleted = false and sp.deleted = false and p.deleted = false ");
        sql.append(" and s.transfer_type not in ('DRAFT','CANCELLED') ");
        if (filter.getWarehouseId() != null) {
            sql.append(" and s.warehouse_id = ").append(filter.getWarehouseId());
        }
        sql.append(" and s.transfer_type = '").append(TransferType.IN.name()).append("'");
        if (filter.isNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(p.name)").append(filter.getSearchKey());
            sql.append(" or lower(p.barcode)").append(filter.getSearchKey());
            sql.append(" or lower(p.vat_barcode)").append(filter.getSearchKey());
            sql.append(") ");
        }
        sql.append(" group by p.id, p.name,p.barcode order by p.id ), ");
        sql.append(" stock_out as ( ");
        sql.append(" select p.id as id, p.name,sum(sp.qty) as qty, avg(selling_price) as sellingPrice,");
        sql.append(" avg(total_selling_price) as totalSellingPrice ");
        sql.append(" from stock_product sp ");
        sql.append(" inner join stock s on s.id = sp.stock_id ");
        sql.append(" inner join product p on sp.product_id = p.id ");
        if (filter.getCategoryId() != null) {
            sql.append(" inner join category c on p.category_id = c.id ");
        }
        sql.append(" where s.company_id =").append(SecurityUtils.getCurrentCompanyId());

        if (filter.getCategoryId() != null) {
            sql.append(" and c.deleted = false and c.id = ").append(filter.getCategoryId());
        }
        if (filter.getWarehouseId() != null) {
            sql.append(" and s.warehouse_id = ").append(filter.getWarehouseId());
        }
        sql.append(" and s.deleted = false and sp.deleted = false and p.deleted = false ");
        sql.append(" and s.transfer_type = '").append(TransferType.OUT.name()).append("'");
        if (filter.isNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(p.name)").append(filter.getSearchKey());
            sql.append(" or lower(p.barcode)").append(filter.getSearchKey());
            sql.append(" or lower(p.vat_barcode)").append(filter.getSearchKey());
            sql.append(") ");
        }
        sql.append(" group by p.id, p.name,p.barcode order by p.id )");
        sql.append(" select si.id, si.name,  si.totalSellingPrice  as sellingPrice, ");
        sql.append(" si.totalSellingPrice * si.qty as totalSellingPrice, ");
        sql.append(" si.totalPurchasePrice as purchasePrice, ");
        sql.append(" si.totalPurchasePrice * si.qty as totalPurchasePrice, ");
        sql.append(" (select so.qty from stock_out so where so.id = si.id) * ");
        sql.append(" (si.totalSellingPrice - (select so.sellingPrice from stock_out so where so.id = si.id)) ");
        sql.append(" as potentialProfit, case when (si.totalPurchasePrice * si.qty) != 0 then ");
        sql.append(" ((select so.qty from stock_out so where so.id = si.id) * (si.totalSellingPrice - ");
        sql.append(" (select so.sellingPrice from stock_out so where so.id = si.id))) / (si.totalPurchasePrice * si.qty) * 100 ");
        sql.append(" else 0 end as potentialProfitPercent,");
        sql.append(" (si.totalSellingPrice - si.totalPurchasePrice) * si.qty as totalProfit, ");
        sql.append(" (select so.qty from stock_out so where so.id = si.id) as soldQty, si.markUpPercent ");
        sql.append(" from stock_in si ");
        sql.append(" order by ").append(filter.getOrderBy()).append(" ").append(filter.getSortOrder().getName());

        Query query = entityManager
                .createNativeQuery(sql.toString(), "StockReportMapper")
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        countSql.append(" with countProduct as ( ");
        countSql.append(" select p.id from stock_product sp ");
        countSql.append(" inner join stock s on s.id = sp.stock_id ");
        countSql.append(" inner join product p on sp.product_id = p.id ");
        if (filter.getCategoryId() != null) {
            countSql.append(" inner join category c on p.category_id = c.id ");
        }
        countSql.append(" where s.company_id = ").append(SecurityUtils.getCurrentCompanyId());
        if (filter.getCategoryId() != null) {
            countSql.append(" and c.deleted = false ");
        }
        if (filter.getWarehouseId() != null) {
            countSql.append(" and s.warehouse_id = ").append(filter.getWarehouseId());
        }
        countSql.append(" and s.deleted = false ");
        countSql.append(" and sp.deleted = false ");
        countSql.append(" and p.deleted = false ");
        countSql.append(" and s.transfer_type = '").append(TransferType.IN.name()).append("' ");
        if (filter.isNotEmpty()) {
            countSql.append(" and (");
            countSql.append(" lower(p.name)").append(filter.getSearchKey());
            countSql.append(" or lower(p.barcode)").append(filter.getSearchKey());
            countSql.append(" or lower(p.vat_barcode)").append(filter.getSearchKey());
            countSql.append(") ");
        }
        countSql.append(" and s.transfer_type not in ('DRAFT','CANCELLED') ");
        countSql.append(" group by p.id) ");
        countSql.append(" select count(cp.id) from countProduct cp ");

        Query countQuery = entityManager.createNativeQuery(countSql.toString());

        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
            countQuery.setParameter("searchKey", filter.getSearchValue());
        }
        String count = countQuery.getSingleResult().toString();

        return new PageImpl<>(query.getResultList(), filter.getPageable(), Long.parseLong(count));
    }

    @Override
    public CommonStockStatisticsDTO getStatistic(StockFilter filter) {
        applyFilter(filter);

        String baseSql = getBaseQuery(filter, false);

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT 'CHECKING_TOTAL' AS dataType, COUNT(DISTINCT s.id) FILTER(WHERE s.status='CHECKING') AS total ");
        sql.append(baseSql);
        sql.append(" UNION ALL ");

        sql.append("SELECT 'CHECKING_ERROR_TOTAL' AS dataType, COUNT(DISTINCT s.id) FILTER(WHERE s.status='CHECKING_ERROR') AS total ");
        sql.append(baseSql);
        sql.append(" UNION ALL ");

        sql.append("SELECT 'DRAFT_TOTAL' AS dataType, COUNT(DISTINCT s.id) FILTER(WHERE s.status='DRAFT') AS total ");
        sql.append(baseSql);
        sql.append(" UNION ALL ");

        sql.append("SELECT 'CANCELLED_TOTAL' AS dataType, COUNT(DISTINCT s.id) FILTER(WHERE s.status='CANCELLED') AS total ");
        sql.append(baseSql);
        sql.append(" UNION ALL ");

        sql.append("SELECT 'COMPLETED_TOTAL' AS dataType, COUNT(DISTINCT s.id) FILTER(WHERE s.status='COMPLETED') AS total ");
        sql.append(baseSql);

        Query query = entityManager.createQuery(sql.toString());
        setParameters((TypedQuery<?>) query, filter);

        CommonStockStatisticsDTO result = new CommonStockStatisticsDTO();
        List<Object[]> resultList = query.getResultList();

        for (Object[] row : resultList) {
            String type = row[0].toString();
            Long count = Long.parseLong(row[1].toString());

            switch (type) {
                case "CHECKING_TOTAL":
                    result.setCheckingTotal(count);
                    break;
                case "CHECKING_ERROR_TOTAL":
                    result.setCheckingErrorTotal(count);
                    break;
                case "DRAFT_TOTAL":
                    result.setDraftTotal(count);
                    break;
                case "CANCELLED_TOTAL":
                    result.setCancelledTotal(count);
                    break;
                case "COMPLETED_TOTAL":
                    result.setCompletedTotal(count);
                    break;
            }
        }

        return result;
    }

    private boolean getAdvanceSearch(StockFilter filter) {
        return StringUtils.hasText(filter.getAggregationCode()) ||
            StringUtils.hasText(filter.getMarkCode()) ||
            StringUtils.hasText(filter.getAggregationBlockCode()) ||
            StringUtils.hasText(filter.getBarcode()) ||
            StringUtils.hasText(filter.getVatBarcode()) ||
            StringUtils.hasText(filter.getAcceptanceTransferActNumber()) ||
            StringUtils.hasText(filter.getReceiptUid()) ||
            StringUtils.hasText(filter.getFactura());
    }
}
