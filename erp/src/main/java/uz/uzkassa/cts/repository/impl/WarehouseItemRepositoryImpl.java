package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import uz.uzkassa.cts.dto.stock.balancereport.StockBalanceReportListDTO;
import uz.uzkassa.cts.filter.StockFilter;
import uz.uzkassa.cts.repository.WarehouseItemRepositoryCustom;
import uz.uzkassa.cts.security.SecurityUtils;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class WarehouseItemRepositoryImpl extends BaseRepositoryImpl implements WarehouseItemRepositoryCustom {

    public Page<StockBalanceReportListDTO> getReport(StockFilter filter) {
        filter.setCompanyId(getCompanyId(filter.getCompanyId()));
        StringBuilder sql = switch (filter.getItemType()) {
            case PRODUCT -> getProductReportBaseSql(filter);
            case DEVICE -> getDeviceReportBaseSql(filter);
            case FISCAL_MODULE -> getFiscalModuleReportBaseSql(filter);
            case ACCESSORY -> getAccessoryReportBaseSql(filter);
        };
        sql.append(" order by itemId");

        Query query = entityManager
                .createNativeQuery(sql.toString(), "StockBalanceReportListMapper")
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        StringBuilder countSql = new StringBuilder();
        countSql.append("select count(t.itemId) from t (");
        countSql.append(getProductReportBaseSql(filter));
        countSql.append(") t ");

        Query countQuery = entityManager.createNativeQuery(countSql.toString());

        return new PageImpl<>(query.getResultList(), filter.getOrderedPageable(), countQuery.getFirstResult());
    }

    private StringBuilder getProductReportBaseSql(StockFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("select p.id as itemId,");
        switch (SecurityUtils.getCurrentRequestLanguageEnum()) {
            case ru -> sql.append("p.name_ru");
            case uz_latin -> sql.append("p.name_uz_latin");
            case uz_cyrillic -> sql.append("p.name_uz_cyrillic");
        }
        sql.append(" as itemName, ");
        sql.append(" w.id as warehouseId, w.name as warehouseName,");
        sql.append(" b.id as branchId, b.name as branchName,");
        sql.append(" c.id as companyId, c.name as companyName,");
        sql.append(" sum(wi.qty) as qty");

        sql.append(" from warehouse_item wi ");
        sql.append(" inner join product p on wi.item_id = p.id");
        sql.append(" inner join warehouse w on w.id = wi.warehouse_id");
        sql.append(" inner join branch b on b.id = w.branch_id");
        sql.append(" inner join company c on c.id = b.company_id");
        sql.append(" where c.id =").append(filter.getCompanyId());
        if (filter.getBranchId() != null) {
            sql.append(" and b.id =").append(filter.getBranchId());
        }
        if (filter.getWarehouseId() != null) {
            sql.append(" and w.id =").append(filter.getWarehouseId());
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" or lower(p.barcode)").append(filter.getLikeSearch());
            sql.append(" or lower(p.sku_number)").append(filter.getLikeSearch());
            sql.append(" or lower(p.upc_number)").append(filter.getLikeSearch());
            sql.append(" or lower(p.part_number)").append(filter.getLikeSearch());
            sql.append(" or lower(p.inventory_number)").append(filter.getLikeSearch());

            switch (SecurityUtils.getCurrentRequestLanguageEnum()) {
                case ru -> sql.append("or lower(p.name_ru)").append(filter.getLikeSearch());
                case uz_latin -> sql.append("or lower(p.name_uz_latin)").append(filter.getLikeSearch());
                case uz_cyrillic -> sql.append("or lower(p.name_uz_cyrillic)").append(filter.getLikeSearch());
            }
            sql.append(") ");
        }
        sql.append(" group by p.id, w.id, b.id, c.id");
        return sql;
    }

    private StringBuilder getDeviceReportBaseSql(StockFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("select d.id as itemId, d.serial_number as name,");
        sql.append(" w.id as warehouseId, w.name as warehouseName,");
        sql.append(" b.id as branchId, b.name as branchName,");
        sql.append(" c.id as companyId, c.name as companyName,");
        sql.append(" sum(wi.qty) as qty");

        sql.append(" from warehouse_item wi ");
        sql.append(" inner join device d on wi.item_id = d.id");
        sql.append(" inner join warehouse w on w.id = wi.warehouse_id");
        sql.append(" inner join branch b on b.id = w.branch_id");
        sql.append(" inner join company c on c.id = b.company_id");
        sql.append(" where c.id =").append(filter.getCompanyId());
        if (filter.getBranchId() != null) {
            sql.append(" and b.id =").append(filter.getBranchId());
        }
        if (filter.getWarehouseId() != null) {
            sql.append(" and w.id =").append(filter.getWarehouseId());
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" or lower(d.serial_number)").append(filter.getLikeSearch());
            sql.append(") ");
        }
        sql.append(" group by d.id, w.id, b.id, c.id");
        return sql;
    }

    private StringBuilder getFiscalModuleReportBaseSql(StockFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("select fm.id as itemId, fm.serial_number as name,");
        sql.append(" w.id as warehouseId, w.name as warehouseName,");
        sql.append(" b.id as branchId, b.name as branchName,");
        sql.append(" c.id as companyId, c.name as companyName,");
        sql.append(" sum(wi.qty) as qty");

        sql.append(" from warehouse_item wi ");
        sql.append(" inner join fiscal_module fm on wi.item_id = fm.id");
        sql.append(" inner join warehouse w on w.id = wi.warehouse_id ");
        sql.append(" inner join branch b on b.id = w.branch_id");
        sql.append(" inner join company c on c.id = b.company_id");
        sql.append(" where c.id =").append(filter.getCompanyId());
        if (filter.getBranchId() != null) {
            sql.append(" and b.id =").append(filter.getBranchId());
        }
        if (filter.getWarehouseId() != null) {
            sql.append(" and w.id =").append(filter.getWarehouseId());
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" or lower(fm.serial_number)").append(filter.getLikeSearch());
            sql.append(") ");
        }
        sql.append(" group by fm.id, w.id, b.id, c.id");
        return sql;
    }

    private StringBuilder getAccessoryReportBaseSql(StockFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("select a.id as itemId, a.name,");
        sql.append(" w.id as warehouseId, w.name as warehouseName,");
        sql.append(" b.id as branchId, b.name as branchName,");
        sql.append(" c.id as companyId, c.name as companyName,");
        sql.append(" sum(wi.qty) as qty");

        sql.append(" from warehouse_item wi ");
        sql.append(" inner join accessory a on wi.item_id = a.id");
        sql.append(" inner join warehouse w on w.id = wi.warehouse_id");
        sql.append(" inner join branch b on b.id = w.branch_id");
        sql.append(" inner join company c on c.id = b.company_id");
        sql.append(" where c.id =").append(filter.getCompanyId());
        if (filter.getBranchId() != null) {
            sql.append(" and b.id =").append(filter.getBranchId());
        }
        if (filter.getWarehouseId() != null) {
            sql.append(" and w.id =").append(filter.getWarehouseId());
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" or lower(a.name)").append(filter.getLikeSearch());
            sql.append(") ");
        }
        sql.append(" group by a.id, w.id, b.id, c.id");
        return sql;
    }


    @Override
    public BigDecimal getReportTotalQty(StockFilter filter) {
        filter.setCompanyId(getCompanyId(filter.getCompanyId()));
        StringBuilder sql = new StringBuilder();
        sql.append("select sum(wi.qty) ");
        sql.append(" from warehouse_item wi ");
        sql.append(" inner join warehouse w on w.id = wi.warehouse_id");
        sql.append(" inner join branch b on b.id = w.branch_id");
        sql.append(" inner join company c on c.id = b.company_id");
        sql.append(" where c.id =").append(filter.getCompanyId());
        if (filter.getBranchId() != null) {
            sql.append(" and b.id =").append(filter.getBranchId());
        }
        if (filter.getWarehouseId() != null) {
            sql.append(" and w.id =").append(filter.getWarehouseId());
        }
        /*if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" or lower(p.barcode)").append(filter.getLikeSearch());
            sql.append(" or lower(p.sku_number)").append(filter.getLikeSearch());
            sql.append(" or lower(p.upc_number)").append(filter.getLikeSearch());
            sql.append(" or lower(p.part_number)").append(filter.getLikeSearch());
            sql.append(" or lower(p.inventory_number)").append(filter.getLikeSearch());

            switch (SecurityUtils.getCurrentRequestLanguageEnum()) {
                case ru -> sql.append("or lower(p.name_ru)").append(filter.getLikeSearch());
                case uz_latin -> sql.append("or lower(p.name_uz_latin)").append(filter.getLikeSearch());
                case uz_cyrillic -> sql.append("or lower(p.name_uz_cyrillic)").append(filter.getLikeSearch());
            }
            sql.append(") ");
        }*/
        Query query = entityManager.createNativeQuery(sql.toString());
        return (BigDecimal) query.getSingleResult();
    }

    @Override
    public BigDecimal getVariationsQty(StockFilter filter) {
        String variationOptions = filter.getVariationOptionIds().stream().map(String::valueOf).collect(Collectors.joining(",", "'{", "}'"));
        StringBuilder sql = new StringBuilder();
        sql.append(" select sum(wi.qty) from warehouse_item wi");
        sql.append(" where wi.item_id =").append(filter.getItemId());
        sql.append(" and wi.unit_id =").append(filter.getUnitId());
        sql.append(" and wi.warehouse_id =").append(filter.getWarehouseId());
        sql.append(" and wi.variation_options @>").append(variationOptions);

        Query query = entityManager.createNativeQuery(sql.toString());
        return (BigDecimal) query.getSingleResult();
    }
}
