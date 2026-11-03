package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import uz.uzkassa.smartpossupply.domain.product.ProductContract;
import uz.uzkassa.smartpossupply.dto.stock.StockProductQtyDetailListDTO;
import uz.uzkassa.smartpossupply.dto.stock.StockProductQtyStatisticsDTO;
import uz.uzkassa.smartpossupply.dto.stockreport.StockProductStatisticsDTO;
import uz.uzkassa.smartpossupply.enums.StockStatus;
import uz.uzkassa.smartpossupply.enums.TransferType;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.filters.StockFilter;
import uz.uzkassa.smartpossupply.repository.ProductContractRepositoryCustom;

import java.util.ArrayList;
import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 20.07.2023
 */
public class ProductContractRepositoryImpl
    extends BaseFilterableRepositoryImpl<ProductContract, BaseFilter>
    implements ProductContractRepositoryCustom {

    @Override
    protected Class<ProductContract> getEntityClass() {
        return ProductContract.class;
    }

    @Override
    protected String getEntityAlias() {
        return "pc";
    }

    @Override
    public void applyFilter(BaseFilter filter) {
        filter.setCompanyId(getCompanyId(filter));
        filter.setBranchId(getBranchId(filter, false));
    }

    @Override
    public String getBaseQuery(BaseFilter filter, boolean includeFetch) {
        StringBuilder sql = new StringBuilder(" FROM ProductContract pc ");

        // Add joins (never use FETCH for filter-based joins)
        if (includeFetch) {
            sql.append("JOIN FETCH pc.product p ")
                .append("JOIN FETCH pc.branch b ");
        } else {
            sql.append("JOIN pc.product p ")
                .append("JOIN pc.branch b ");
        }

        // Base conditions
        sql.append("WHERE pc.deleted = false ")
            .append("AND p.deleted = false ")
            .append("AND b.deleted = false ")
            .append("AND b.companyId = :companyId ");

        // Dynamic conditions
        appendCondition(sql, filter.getBranchId() != null, "AND b.id = :branchId ");
        appendCondition(sql, filter.getProductId() != null, "AND pc.productId = :productId ");

        // Search conditions
        if (filter.isNotEmpty()) {
            sql.append("AND (")
                .append("LOWER(pc.name)").append(filter.getSearchKey())
                .append(" OR LOWER(p.name)").append(filter.getSearchKey())
                .append(" OR LOWER(p.barcode)").append(filter.getSearchKey())
                .append(" OR LOWER(p.vatBarcode)").append(filter.getSearchKey())
                .append(") ");
        }

        return sql.toString();
    }

    @Override
    public void setParameters(TypedQuery<?> query, BaseFilter filter) {
        // Required parameter
        query.setParameter("companyId", filter.getCompanyId());

        // Optional parameters
        setParameterIfNotNull(query, "branchId", filter.getBranchId());
        setParameterIfNotNull(query, "productId", filter.getProductId());

        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
        }
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
            }
        });
        return result;
    }

    private StringBuilder getStatisticsBaseSql(StockFilter filter, TransferType transferType) {
        StringBuilder sql = new StringBuilder();
        sql.append(" from stock s ");
        sql.append(" inner join company c");
        if (TransferType.OUT.equals(filter.getTransferType())) {
            sql.append(" on s.customer_id = c.id ");
        } else if (TransferType.IN.equals(filter.getTransferType())) {
            sql.append(" on s.supplier_id = c.id ");
        } else {
            sql.append(" on s.company_id = c.id ");
        }
        sql.append(" inner join stock_product sp on sp.stock_id = s.id ");
        sql.append(" inner join product_contract pc on pc.id=sp.product_contract_id");
        sql.append(" where pc.deleted = false and sp.deleted =false and s.deleted =false ");
        sql.append(" and s.stock_transfer_id is null");
        sql.append(" and pc.id = ").append(filter.getProductContractId());
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
        sql.append("select s.id as id, s.stock_number stockNumber, s.transfer_date as stockDate, sp.qty as qty, c.id as companyId, c.name as companyName, c.tin as companyTin");
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
