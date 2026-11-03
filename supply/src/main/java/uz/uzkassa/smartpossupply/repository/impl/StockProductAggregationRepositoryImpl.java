package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.smartpossupply.domain.stock.StockProductAggregation;
import uz.uzkassa.smartpossupply.dto.base.CommonNameDTO;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.product.ProductCapacityDTO;
import uz.uzkassa.smartpossupply.dto.stock.StockProductLookUpDTO;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.enums.StockStatus;
import uz.uzkassa.smartpossupply.enums.TransferType;
import uz.uzkassa.smartpossupply.enums.TuronMarkStatus;
import uz.uzkassa.smartpossupply.filters.AggregationCodesReportFilter;
import uz.uzkassa.smartpossupply.filters.StockInProductFilter;
import uz.uzkassa.smartpossupply.repository.StockProductAggregationRepositoryCustom;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.util.List;

public class StockProductAggregationRepositoryImpl extends BaseRepositoryImpl implements StockProductAggregationRepositoryCustom {

    @Override
    public List<StockProductLookUpDTO> getLookUp(StockInProductFilter filter) {
        boolean enabledSorting = filter.isEnabledSorting();
        filter.setCompanyId(getCompanyId(filter));
        filter.setBranchId(getBranchId(filter, false));
        filter.setWarehouseId(getWarehouseId(filter, false));

        StringBuilder sql = new StringBuilder();
        sql.append("select p.id, ");
        sql.append(" p.name, ");

        sql.append(" wp.qty qtyInWarehouse, ");
        sql.append(" spa.reservation_qty reservationQty, ");
        sql.append(" spa.qty aggregationOrMarkQty, ");

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
            sql.append(" 0 as sortOrder, ");
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

        sql.append(" from stock_product_aggregation spa ");
        sql.append(" inner join stock_product sp on sp.id = spa.stock_product_id ");
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
        sql.append(" where p.deleted = false and s.deleted = false ");
        sql.append(" and p.status='").append(CommonStatus.ACTIVE.getCode()).append("'");
        sql.append(" and s.transfer_type='").append(filter.getTransferType()).append("' ");
        sql.append(" and s.company_id = ").append(filter.getCompanyId());
        sql.append(" and (spa.code ='").append(filter.getAggregationCode().toLowerCase()).append("' or spa.print_code ='").append(filter.getAggregationCode()).append("')");
        if (filter.getWarehouseId() != null) {
            sql.append(" and wp.warehouse_id=").append(filter.getWarehouseId());
        }
        if (filter.getBranchId() != null) {
            sql.append(" and p.branch_id=").append(filter.getBranchId());
        }
        if (filter.getCompanyCommittentId() != null) {
            sql.append(" and cc.id =").append(filter.getCompanyCommittentId());
        }
        if (filter.getCustomerId() != null && TransferType.OUT.name().equals(filter.getTransferType())) {
            sql.append(" and s.customer_id =").append(filter.getCustomerId());
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
        return query.getResultList();
    }

    @Override
    public List<ProductCapacityDTO> lookUpProducts(StockInProductFilter filter) {
        boolean enabledSorting = filter.isEnabledSorting();
        filter.setCompanyId(getCompanyId(filter));
        filter.setBranchId(getBranchId(filter, false));
        filter.setWarehouseId(getWarehouseId(filter, false));

        StringBuilder sql = new StringBuilder();
        sql.append("select p.id, p.name, p.capacity ");
        if (enabledSorting) {
            sql.append(", ps.sort_order sortOrder ");
        }

        sql.append(" from stock_product_aggregation spa ");
        sql.append(" inner join stock_product sp on sp.id = spa.stock_product_id ");
        sql.append(" inner join stock s on s.id = sp.stock_id ");
        sql.append(" inner join product p on p.id = sp.product_id ");
        sql.append(" inner join warehouse_product wp on wp.product_id = p.id ");
        if (enabledSorting) {
            sql.append(" left join product_sort ps on p.id = ps.product_id ");
            if (filter.getEnvironmentType() != null) {
                sql.append("and ps.environment_type = '").append(filter.getEnvironmentType()).append("'");
            }
            if (filter.getSortOrderType() != null) {
                sql.append(" and ps.sort_order_type = '").append(filter.getSortOrderType()).append("'");
            }
        }
        sql.append(" where p.deleted = false and s.deleted = false ");
        sql.append(" and p.status='").append(CommonStatus.ACTIVE.getCode()).append("'");
        sql.append(" and s.transfer_type='").append(filter.getTransferType()).append("' ");
        sql.append(" and s.company_id = ").append(filter.getCompanyId());
        sql.append(" and (spa.code ='").append(filter.getAggregationCode().toLowerCase()).append("' or spa.print_code ='").append(filter.getAggregationCode()).append("')");
        if (filter.getWarehouseId() != null) {
            sql.append(" and wp.warehouse_id=").append(filter.getWarehouseId());
        }
        if (filter.getBranchId() != null) {
            sql.append(" and p.branch_id=").append(filter.getBranchId());
        }
        if (filter.getCustomerId() != null && TransferType.OUT.name().equals(filter.getTransferType())) {
            sql.append(" and s.customer_id =").append(filter.getCustomerId());
        }
//        if (!CollectionUtils.isEmpty(filter.getProductTypes())){
//            sql.append(" and p.product_type not in (").append(filter.getProductTypes().stream().map(productType ->  "'" + productType + "'").collect(Collectors.joining(","))).append(" )");
//        }
        sql.append(" group by p.id");
        if (enabledSorting) {
            sql.append(", ps.sort_order order by ps.sort_order");
        } else {
            sql.append(" order by p.name");
        }

        Query query = entityManager
                .createNativeQuery(sql.toString(), "ProductCapacityDTOMapper")
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());
        return query.getResultList();
    }

    @Override
    public List<CommonNameDTO> lookUpStockProduct(StockInProductFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("select distinct sp.name ");
        sql.append(" from stock_product_aggregation spa ");
        sql.append(" inner join stock_product sp on sp.id = spa.stock_product_id ");
        sql.append(" inner join stock s on s.id = sp.stock_id ");
        sql.append(" inner join product p on p.id = sp.product_id ");
        sql.append(" where p.deleted =false and s.deleted =false ");
        sql.append(" and sp.name is not null ");
        sql.append(" and p.id =").append(filter.getProductId());
        sql.append(" and s.transfer_type='").append(filter.getTransferType()).append("' ");
        sql.append(" and (spa.code ='").append(filter.getAggregationCode()).append("' or spa.print_code ='").append(filter.getAggregationCode()).append("')");
        sql.append(" order by sp.name");

        Query query = entityManager.createNativeQuery(sql.toString(), "CommonNameDTOMapper");
        query.setFirstResult(filter.getStart()).setMaxResults(filter.getSize());
        return query.getResultList();
    }

    @Override
    public ResultList<StockProductAggregation> getCodes(AggregationCodesReportFilter filter) {
        filter.setWarehouseId(getWarehouseId(filter));
        ResultList<StockProductAggregation> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select spa ");
        sql.append(" from StockProductAggregation spa");
        sql.append(" join StockProduct sp on spa.stockProductId = sp.id ");
        sql.append(" join Stock s on sp.stockId = s.id");
        sql.append(" where spa.deleted = false");
        sql.append(" and s.deleted = false");
        sql.append(" and spa.used = false");
        sql.append(" and s.status = '").append(StockStatus.COMPLETED.getCode()).append("'");
        sql.append(" and s.transferType = '").append(TransferType.IN).append("'");
        sql.append(" and spa.productId = :productId");
        sql.append(" and s.warehouseId = :warehouseId");
        if (filter.getProductSerialId() != null) {
            sql.append(" and sp.productSerialId = :productSerialId");
        }
        if (filter.getProductContractId() != null) {
            sql.append(" and sp.productContractId = :productContractId");
        }
        if (filter.getCommittentId() != null) {
            sql.append(" and sp.companyCommittentId = :companyCommittentId");
        }
        if (filter.isParent()) {
            sql.append(" and spa.parent is true");
        }
        if (filter.getPalletId() != null) {
            sql.append(" and spa.palletId = :palletId");
        }
        if (filter.getSupplierId() != null) {
            sql.append(" and s.supplierId = :supplierId");
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and spa.status = :status");
        }
        if (filter.getFrom() != null) {
            sql.append(" and s.transferDate >= :fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and s.transferDate <= :toDate");
        }
        if (filter.isNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(spa.code)").append(filter.getSearchKey());
            sql.append(" or lower(spa.originalCode)").append(filter.getSearchKey());
            sql.append(" or lower(spa.ownerName)").append(filter.getSearchKey());
            sql.append(" or lower(spa.ownerTin)").append(filter.getSearchKey());
            sql.append(")");
        }
        String countSql = sql.toString().replaceFirst("select spa ", "select count(spa.id) ");
        sql.append(" order by s.transferDate desc");

        TypedQuery<StockProductAggregation> query = entityManager.createQuery(sql.toString(), StockProductAggregation.class)
            .setFirstResult(filter.getStart())
            .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        query.setParameter("warehouseId", filter.getWarehouseId());
        countQuery.setParameter("warehouseId", filter.getWarehouseId());

        query.setParameter("productId", filter.getProductId());
        countQuery.setParameter("productId", filter.getProductId());

        if (filter.getProductSerialId() != null) {
            query.setParameter("productSerialId", filter.getProductSerialId());
            countQuery.setParameter("productSerialId", filter.getProductSerialId());
        }
        if (filter.getProductContractId() != null) {
            query.setParameter("productContractId", filter.getProductContractId());
            countQuery.setParameter("productContractId", filter.getProductContractId());
        }
        if (filter.getCommittentId() != null) {
            query.setParameter("companyCommittentId", filter.getCommittentId());
            countQuery.setParameter("companyCommittentId", filter.getCommittentId());
        }
        if (filter.getSupplierId() != null) {
            query.setParameter("supplierId", filter.getSupplierId());
            countQuery.setParameter("supplierId", filter.getSupplierId());
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("status", TuronMarkStatus.valueOf(filter.getStatus()));
            countQuery.setParameter("status", TuronMarkStatus.valueOf(filter.getStatus()));
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
        Long count = countQuery.getSingleResult();
        if (count > 0) {
            resultList.setList(query.getResultList());
            resultList.setCount(count);
        }
        return resultList;
    }

    @Override
    public ProductCapacityDTO getProduct(Long branchId, Long warehouseId, String code, TransferType transferType) {
        StringBuilder sql = new StringBuilder()
            .append("SELECT p.id, p.name, p.capacity ")
            .append("FROM product p ")
            .append("WHERE p.deleted = false ")
            .append("  AND p.status = :status ")
            .append("  AND p.branch_id = :branchId ")
            .append("  AND EXISTS ( ")
            .append("      SELECT 1 FROM stock_product_aggregation spa ")
            .append("      JOIN stock_product sp on spa.stock_product_id = sp.id ")
            .append("      JOIN stock s ON s.id = sp.stock_id ")
            .append("      WHERE spa.product_id = p.id ")
            .append("        AND sp.deleted = false ")
            .append("        AND s.deleted = false ")
            .append("        AND s.transfer_type = :transferType ")
            .append("        AND s.company_id = :companyId ")
            .append("        AND (spa.code = :code or spa.print_code = :printCode) ")
            .append("  ) ")
            .append("  AND EXISTS ( ")
            .append("      SELECT 1 FROM warehouse_product wp ")
            .append("      WHERE wp.product_id = p.id ")
            .append("        AND wp.warehouse_id = :warehouseId ")
            .append("        AND wp.deleted = false ")
            .append("  )");

        Query query = entityManager.createNativeQuery(sql.toString(), "ProductCapacityDTOMapper")
            .setParameter("status", CommonStatus.ACTIVE.getCode())
            .setParameter("companyId", SecurityUtils.getCompanyId())
            .setParameter("branchId", branchId)
            .setParameter("warehouseId", warehouseId)
            .setParameter("code", code)
            .setParameter("printCode", code)
            .setParameter("transferType", transferType.name());
        query.setMaxResults(1);

        List<ProductCapacityDTO> result = query.getResultList();
        if (CollectionUtils.isEmpty(result)) {
            return null;
        }
        return result.getFirst();
    }
}
