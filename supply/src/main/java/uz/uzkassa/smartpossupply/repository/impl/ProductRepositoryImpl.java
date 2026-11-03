package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.smartpossupply.domain.product.Product;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.product.ProductListForOfflineDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductLookUpWithWarehouseQtyDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductPhotoListDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductResidueDTO;
import uz.uzkassa.smartpossupply.dto.product.TopProductDTO;
import uz.uzkassa.smartpossupply.dto.product.WebKassaProductListDTO;
import uz.uzkassa.smartpossupply.dto.stocktransfer.StockTransferProductListDTO;
import uz.uzkassa.smartpossupply.dto.telegram.TelegramTopProductListDTO;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.enums.EnvironmentType;
import uz.uzkassa.smartpossupply.enums.ProductType;
import uz.uzkassa.smartpossupply.enums.SortOrderType;
import uz.uzkassa.smartpossupply.enums.StockStatus;
import uz.uzkassa.smartpossupply.enums.Vat;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.filters.ProductFilter;
import uz.uzkassa.smartpossupply.repository.ProductRepositoryCustom;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProductRepositoryImpl extends BaseRepositoryImpl implements ProductRepositoryCustom {

    @Override
    public ResultList<Product> getResultList(ProductFilter filter) {
        boolean enabledSorting = filter.isEnabledSorting();
        if (!SecurityUtils.isFromMarketplace()) {
            filter.setCompanyId(getCompanyId(filter));
            filter.setBranchId(getBranchId(filter, false));
        }

        ResultList<Product> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select p ");
        sql.append(getProductBaseSql(filter));

        String countSql = sql.toString().replaceFirst("select p", "select count(p.id)");
        if (enabledSorting) {
            sql.append(" order by ps.sortOrder");
        } else {
            sql.append(" order by p.").append(filter.getOrderBy());
            sql.append(" ").append(filter.getSortOrder().getName());
        }

        TypedQuery<Product> query = entityManager
                .createQuery(sql.toString(), Product.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        query.setParameter("companyId", filter.getCompanyId());
        countQuery.setParameter("companyId", filter.getCompanyId());

        if (filter.getBranchId() != null) {
            query.setParameter("branchId", filter.getBranchId());
            countQuery.setParameter("branchId", filter.getBranchId());
        }
        if (filter.getCategoryId() != null) {
            query.setParameter("categoryId", filter.getCategoryId());
            countQuery.setParameter("categoryId", filter.getCategoryId());
        }
        if (filter.getCategoryId() != null && filter.getEnvironmentType() != null) {
            query.setParameter("environmentType", EnvironmentType.valueOf(filter.getEnvironmentType()));
            countQuery.setParameter("environmentType", EnvironmentType.valueOf(filter.getEnvironmentType()));
        }
        if (filter.getFavourite() != null) {
            query.setParameter("favourite", filter.getFavourite());
            countQuery.setParameter("favourite", filter.getFavourite());
        }
        if (filter.getProductType() != null) {
            query.setParameter("productType", ProductType.valueOf(filter.getProductType()));
            countQuery.setParameter("productType", ProductType.valueOf(filter.getProductType()));
        } else if (!CollectionUtils.isEmpty(filter.getProductTypes())) {
            query.setParameter("productTypes", filter.getProductTypes());
            countQuery.setParameter("productTypes", filter.getProductTypes());
        }
        if (filter.getCompanyCommittentId() != null) {
            query.setParameter("companyCommittentId", filter.getCompanyCommittentId());
            countQuery.setParameter("companyCommittentId", filter.getCompanyCommittentId());
        }
        if (filter.getVat() != null) {
            query.setParameter("vat", Vat.valueOf(filter.getVat()));
            countQuery.setParameter("vat", Vat.valueOf(filter.getVat()));
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("status", CommonStatus.valueOf(filter.getStatus()));
            countQuery.setParameter("status", CommonStatus.valueOf(filter.getStatus()));
        }
        if (enabledSorting) {
            if (filter.getEnvironmentType() != null) {
                query.setParameter("environmentType", EnvironmentType.valueOf(filter.getEnvironmentType()));
                countQuery.setParameter("environmentType", EnvironmentType.valueOf(filter.getEnvironmentType()));
            }
            if (filter.getSortOrderType() != null) {
                query.setParameter("sortOrderType", SortOrderType.valueOf(filter.getSortOrderType()));
                countQuery.setParameter("sortOrderType", SortOrderType.valueOf(filter.getSortOrderType()));
            }
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
    public ResultList<ProductListForOfflineDTO> getResultListForOffline(ProductFilter filter) {
        ResultList<ProductListForOfflineDTO> resultList = new ResultList<>();
        filter.setCompanyId(getCompanyId(filter));
        filter.setBranchIds(getBranchIds(false));

        // Build query to get product IDs for current page
        StringBuilder idSql = new StringBuilder("""
                    select p.id
                    from product p
                    where p.deleted = false
                      and p.status = 'ACTIVE'
                """);

        if (filter.getLastSyncDate() != null) {
            idSql.append(" and (p.created_date >= :lastSyncDate or p.updated_date >= :lastSyncDate) ");
        }
        if (filter.getCompanyId() != null) {
            idSql.append(" and p.company_id = :companyId ");
        }
        if (filter.getBranchIds() != null) {
            idSql.append(" and p.branch_id in (:branchIds) ");
        }

        idSql.append(" order by p.id limit :limit offset :offset");

        Query idQuery = entityManager.createNativeQuery(idSql.toString());
        idQuery.setParameter("limit", filter.getSize());
        idQuery.setParameter("offset", filter.getStart());
        if (filter.getLastSyncDate() != null) {
            idQuery.setParameter("lastSyncDate", filter.getLastSyncDate());
        }
        if (filter.getCompanyId() != null) {
            idQuery.setParameter("companyId", filter.getCompanyId());
        }
        if (filter.getBranchIds() != null) {
            idQuery.setParameter("branchIds", filter.getBranchIds());
        }


        List<Long> productIds = idQuery.getResultList();

        if (productIds.isEmpty()) {
            resultList.setList(Collections.emptyList());
            resultList.setCount(0L);
            return resultList;
        }

        // Build main get product query
        String sql = """
                    select p.id,
                           (p.name || ' ' || coalesce(p.capacity,'')) as name,
                           p.barcode,
                           p.vat,
                           p.favourite,
                           u.id as unitId,
                           u.code as unitCode,
                           u.name_ru as unitName,
                           u.measure_id as measureId,
                           p.purchase_price as purchasePrice,
                           p.price,
                           p.foreign_price as foreignPrice,
                           p.excise_rate as exciseRate,
                           p.excise_sum as exciseSum,
                           coalesce(sum(wp.qty), 0) as qtyInWarehouse,
                           coalesce(sum(wp.reservation_qty), 0) as reservationQty,
                           p.product_type as productType,
                           p.has_mark as hasMark,
                           p.qty_in_package as qtyInPackage,
                           p.qty_in_box as qtyInBox,
                           p.qty_in_pallet as qtyInPallet,
                           p.weight_brutto as brutto,
                           p.weight_netto as netto,
                           p.box_barcode as boxBarcode,
                           p.block_barcode as blockBarcode,
                           p.block_products as blockProducts,
                           p.capacity as capacity,
                           array_remove(array_agg(distinct pc.category_id), null)::bigint[] as categoryIds,
                           array_remove(array_agg(distinct wp.warehouse_id), null)::bigint[] as warehouseIds,
                           array_remove(array_agg(distinct p.branch_id), null)::bigint[] as branchIds
                    from product p
                             left join product_category pc on pc.product_id = p.id
                             left join warehouse_product wp on wp.product_id = p.id
                             left join unit u on u.id = p.unit_id
                    where p.id in (:ids)
                    group by p.id, u.id, u.code, u.name_ru, u.measure_id
                    order by p.id
                """;


        Query query = entityManager.createNativeQuery(sql, "ProductListForOfflineMapper");
        query.unwrap(org.hibernate.query.NativeQuery.class)
                .setParameterList("ids", productIds);

        @SuppressWarnings("unchecked")
        List<ProductListForOfflineDTO> data = query.getResultList();

        // Group photos by product
        String photoSql = """
                    select 
                        pp.product_id as product_id,
                        f.id,
                        f.url,
                        pp.is_main as main
                    from product_photo pp
                             join file_upload f on pp.photo_id = f.id
                    where pp.product_id in (:ids)
                    order by pp.product_id, pp.is_main desc
                """;

        Query photoQuery = entityManager.createNativeQuery(photoSql, "ProductPhotoListMapper");
        photoQuery.unwrap(org.hibernate.query.NativeQuery.class)
                .setParameterList("ids", productIds);

        @SuppressWarnings("unchecked")
        List<ProductPhotoListDTO> allPhotos = photoQuery.getResultList();

        Map<Long, List<ProductPhotoListDTO>> photosMap = allPhotos.stream()
                .collect(Collectors.groupingBy(ProductPhotoListDTO::getProductId));

        data.forEach(dto ->
                dto.setPhotos(photosMap.getOrDefault(dto.getId(), Collections.emptyList()))
        );

        // Count total products for pagination
        StringBuilder countSql = new StringBuilder("""
                    select count(p.id) 
                    from product p
                    where p.deleted = false and p.status = 'ACTIVE'
                """);

        if (filter.getLastSyncDate() != null) {
            countSql.append(" and (p.created_date >= :lastSyncDate or p.updated_date >= :lastSyncDate) ");
        }
        if (filter.getCompanyId() != null) {
            countSql.append(" and p.company_id = :companyId ");
        }
        if (filter.getBranchIds() != null) {
            countSql.append(" and p.branch_id in (:branchIds) ");
        }

        Query countQuery = entityManager.createNativeQuery(countSql.toString());
        if (filter.getLastSyncDate() != null) {
            countQuery.setParameter("lastSyncDate", filter.getLastSyncDate());
        }
        if (filter.getCompanyId() != null) {
            countQuery.setParameter("companyId", filter.getCompanyId());
        }
        if (filter.getBranchIds() != null) {
            countQuery.setParameter("branchIds", filter.getBranchIds());
        }

        // Get total count
        Long count = ((Number) countQuery.getSingleResult()).longValue();

        resultList.setList(data);
        resultList.setCount(count);
        return resultList;
    }

    private StringBuilder getProductBaseSql(ProductFilter filter) {
        boolean enabledSorting = filter.isEnabledSorting();
        StringBuilder sql = new StringBuilder();
        sql.append(" from Product p ");
        if (enabledSorting) {
            sql.append(" left join ProductSort ps on p.id = ps.productId ");
            if (filter.getEnvironmentType() != null) {
                sql.append("and ps.environmentType = :environmentType ");
            }
            if (filter.getSortOrderType() != null) {
                sql.append(" and ps.sortOrderType = :sortOrderType ");
            }
        }
        if (filter.getCategoryId() != null) {
            sql.append(" join p.categories pc ");
        }
        sql.append(" where p.deleted =false");
        sql.append(" and p.companyId =:companyId");
        if (filter.getBranchId() != null) {
            sql.append(" and p.branchId=:branchId ");
        }
        if (filter.getCategoryId() != null) {
//            sql.append(" and (p.categoryId=:categoryId ");
//            sql.append(" or p.category.parentId in (select id from Category c where c.parentId=:categoryId)");
//            sql.append(" or p.category.parentId in (select id from Category c where c.id=:categoryId)");
            sql.append(" and pc.id = :categoryId");
        }
        if (filter.getCategoryId() != null && filter.getEnvironmentType() != null) {
            sql.append(" and pc.environmentType = :environmentType");
        }
        if (filter.getFavourite() != null) {
            sql.append(" and p.favourite=:favourite ");
        }
        if (filter.getValidateProductType() != null && filter.getValidateProductType()) {
            sql.append(" and p.productType is not null ");
        }
        if (filter.getProductType() != null) {
            sql.append(" and p.productType=:productType ");
        } else if (!CollectionUtils.isEmpty(filter.getProductTypes())) {
            sql.append(" and (p.productType not in (:productTypes) or p.productType is null ) ");
        }
        if (filter.getCompanyCommittentId() != null) {
            sql.append(" and p.companyCommittentId=:companyCommittentId ");
        }
        if (filter.getHasMark() != null) {
            if (filter.getHasMark()) {
                sql.append(" and p.hasMark = true");
            } else {
                sql.append(" and p.hasMark = false");
            }
        }
        if (filter.getHasCategory() != null) {
            if (filter.getHasCategory()) {
                sql.append(" and p.categories is not empty");
            } else {
                sql.append(" and p.categories is empty");
            }
        }
        if (filter.getVat() != null) {
            sql.append(" and p.vat=:vat");
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and p.status =:status");
        }
        if (filter.isNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(p.name)").append(filter.getSearchKey());
            sql.append(" or lower(p.barcode)").append(filter.getSearchKey());
            sql.append(" or lower(p.vatBarcode)").append(filter.getSearchKey());
            sql.append(")");
        }
        return sql;
    }

    public List<ProductResidueDTO> getProductResidueList(BaseFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append(" with warehouse_sum_products as (select case when sum(wp.qty)=0 then 1 else sum(wp.qty) end sum_qty ");
        sql.append(" from warehouse_product wp ");
        sql.append(" inner join warehouse w on w.id = wp.warehouse_id ");
        sql.append(" inner join product p on p.id = wp.product_id ");
        sql.append(" where p.company_id = :companyId and wp.deleted = false and w.deleted = false and p.deleted = false ");

        if (filter.getBranchId() != null) {
            sql.append(" and w.branch_id = :branchId ");
        }

        sql.append(") ");
        sql.append(
                "select p.id, p.name, sum(wp.qty) as qty , sum(wp.qty) / (select sum_qty from warehouse_sum_products) * 100 as percentage, "
        );
        switch (SecurityUtils.getCurrentRequestLanguageEnum()) {
            case uz:
                sql.append(" u.name_uz ");
                break;
            case cyrillic:
                sql.append(" u.name_cyrillic ");
                break;
            case ru:
                sql.append(" u.name_ru ");
                break;
        }
        sql.append(" as unitName ");
        sql.append(" from warehouse_product wp ");
        sql.append(" inner join warehouse w on w.id = wp.warehouse_id ");
        sql.append(" inner join product p on p.id = wp.product_id ");
        sql.append(" inner join unit u on u.id = p.unit_id ");
        sql.append(" where p.company_id =:companyId and wp.deleted = false and w.deleted = false and p.deleted = false ");

        if (filter.getBranchId() != null) {
            sql.append(" and w.branch_id = :branchId ");
        }

        sql.append(" group by p.id, u.name_uz, u.name_ru, u.name_cyrillic order by qty desc ");
        Query query = entityManager.createNativeQuery(sql.toString(), "ProductResidueMapper").setFirstResult(0).setMaxResults(20);
        query.setParameter("companyId", SecurityUtils.getCurrentCompanyId());

        if (filter.getBranchId() != null) {
            query.setParameter("branchId", filter.getBranchId());
        }

        return query.getResultList();
    }


    public List<TopProductDTO> getTopProductList(BaseFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append(" with factura_sum_qty as (select sum(fp.qty) totalQty ");
        sql.append(" from factura_product fp ");
        sql.append(" inner join factura_detail fd on fd.id = fp.factura_detail_id ");
        sql.append(" inner join factura f on f.id = fd.factura_id ");
        sql.append(" where f.company_id = :companyId ");
        sql.append(" and f.deleted = false ");
        sql.append(" and not f.status in ('DRAFT', 'CANCELLED') ");

        if (filter.getBranchId() != null) {
            sql.append(" and f.branch_id = :branchId ");
        }
        if (filter.getFrom() != null) {
            sql.append(" and f.created_date >= :fromDate ");
        }
        if (filter.getTo() != null) {
            sql.append(" and f.created_date <= :toDate ");
        }
        sql.append(") ");

        sql.append(" select fp.product_id as productId, p.name as productName,");
        sql.append(" sum(fp.qty) / (select totalQty from factura_sum_qty) *100 as percentage,");
        sql.append(" sum(fp.qty) as qty, ");
        String unitName = switch (SecurityUtils.getCurrentRequestLanguageEnum()) {
            case uz -> "u.name_uz";
            case cyrillic -> "u.name_cyrillic";
            default -> "u.name_ru";
        };
        sql.append(unitName).append(" as unitName ");
        sql.append(" from factura_product fp ");
        sql.append(" inner join factura_detail fd on fd.id = fp.factura_detail_id ");
        sql.append(" inner join factura f on f.id = fd.factura_id ");
        sql.append(" inner join product p on fp.product_id = p.id ");
        sql.append(" inner join unit u on u.id = p.unit_id ");
        sql.append(" where f.company_id = :companyId ");
        sql.append(" and f.deleted = false ");
        sql.append(" and not f.status in ('DRAFT', 'CANCELLED') ");

        if (filter.getBranchId() != null) {
            sql.append(" and f.branch_id = :branchId ");
        }
        if (filter.getFrom() != null) {
            sql.append(" and f.created_date >= :fromDate ");
        }
        if (filter.getTo() != null) {
            sql.append(" and f.created_date <= :toDate ");
        }

        sql.append(" group by fp.product_id, p.name, ").append(unitName);
        sql.append(" order by qty desc ");
        Query query = entityManager.createNativeQuery(sql.toString(), "TopProductMapper")
                .setFirstResult(0)
                .setMaxResults(20);
        query.setParameter("companyId", filter.getCompanyId());

        if (filter.getBranchId() != null) {
            query.setParameter("branchId", filter.getBranchId());
        }
        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getTo());
        }
        return query.getResultList();
    }

    public List<TelegramTopProductListDTO> getTopProductsForTelegram(Long branchId) {
        StringBuilder sql = new StringBuilder();
        sql.append("select p.id, p.name, p.vat as vat, p.price, p.foreign_price as foreignPrice, p.currency_type as currencyType, f.url as photoUrl ");
        sql.append(" from product p ")
                .append(" inner join stock_product sp on p.id = sp.product_id ")
                .append(" inner join stock s on sp.stock_id = s.id ")
                .append(" left join product_photo pp on pp.product_id = p.id and pp.is_main = true ")
                .append(" left join file_upload f on f.id = pp.photo_id ")
                .append(" where s.deleted = false ")
                .append(" and p.deleted = false ")
                .append(" and s.status =:status")
                .append(" and p.branch_id = :branchId ")
                .append(" GROUP BY p.id, p.name, p.vat, p.price, p.foreign_price, p.currency_type, f.url ")
                .append(" ORDER BY sum(sp.qty) DESC ");

        Query query = entityManager.createNativeQuery(sql.toString(), "TelegramTopProductMapper")
                .setFirstResult(0)
                .setMaxResults(10);

        query.setParameter("status", StockStatus.COMPLETED.name());
        query.setParameter("branchId", branchId);
        return query.getResultList();
    }

    @Override
    public List<ProductLookUpWithWarehouseQtyDTO> getProductLookUpWithWarehouseQty(ProductFilter filter) {
        boolean enabledSorting = filter.isEnabledSorting();
        StringBuilder sql = new StringBuilder();
        sql.append("select p.id, ");
        sql.append(" (p.name||' '|| coalesce(p.capacity,'')) as name, ");
        sql.append(" p.barcode as barcode,");
        sql.append(" p.vat,");
        sql.append(" p.favourite as favourite,");
        sql.append(" u.id as unitId, u.code as unitCode, u.name_ru as unitName,");
        sql.append(" u.measure_id as measureId,");
        sql.append(" p.purchase_price as purchasePrice,");
        sql.append(" p.price,");
        sql.append(" p.foreign_price as foreignPrice,");
        sql.append(" p.excise_rate as exciseRate,");
        sql.append(" p.excise_sum as exciseSum,");
        sql.append(" wp.qty as qtyInWarehouse,");
        sql.append(" wp.reservation_qty as reservationQty,");
        sql.append(" pp.id as parentId, pp.name as parentName, pp.barcode as parentBarcode, pp.vat_barcode as parentVatBarcode,");
        sql.append(" p.product_type as productType,");
        sql.append(" p.has_mark as hasMark,");
        sql.append(" p.qty_in_package as qtyInPackage,");
        sql.append(" p.qty_in_box as qtyInBox,");
        sql.append(" p.qty_in_pallet as qtyInPallet,");
        sql.append(" p.weight_brutto as brutto,");
        sql.append(" p.weight_netto as netto,");
        sql.append(" p.box_barcode as boxBarcode,");
        sql.append(" p.block_barcode as blockBarcode,");
        sql.append(" p.block_products as blockProducts");
        sql.append(" from product p ");
        sql.append(" inner join product_category pc on p.id = pc.product_id ");
        sql.append(" inner join category c on pc.category_id = c.id ");
        sql.append(" inner join warehouse_product wp on p.id = wp.product_id ");
        sql.append(" inner join warehouse w on w.id = wp.warehouse_id ");
        sql.append(" inner join unit u on u.id = wp.unit_id ");
        sql.append(" left join product pp on p.parent_id = pp.id ");
        if (enabledSorting) {
            sql.append(" left join product_sort ps on ps.product_id = p.id ");
            if (filter.getEnvironmentType() != null) {
                sql.append(" and ps.environment_type = '").append(filter.getEnvironmentType()).append("'");
            }
            if (filter.getSortOrderType() != null) {
                sql.append(" and ps.sort_order_type = '").append(filter.getSortOrderType()).append("'");
            }
        }
        sql.append(" where p.deleted = false and w.deleted = false ");
        sql.append(" and p.status='").append(CommonStatus.ACTIVE.getCode()).append("'");
        sql.append(" and p.company_id = ").append(SecurityUtils.getCurrentCompanyId());
        if (filter.getBranchId() != null) {
            sql.append(" and p.branch_id = ").append(filter.getBranchId());
        }
        if (filter.getCategoryId() != null) {
            sql.append(" and c.id = ").append(filter.getCategoryId());
        }
        if (filter.getFavourite() != null) {
            sql.append(" and p.favourite = ").append(filter.getFavourite());
        }
        if (filter.isHideZero()) {
            sql.append(" and wp.qty-abs(coalesce(wp.reservation_qty,0))>0 ");
        }
        sql.append(" and w.id = ").append(filter.getWarehouseId());
        if (!CollectionUtils.isEmpty(filter.getProductTypes())) {
            sql.append(" and (p.product_type is null or p.product_type not in (").append(filter.getProductTypes().stream().map(productType -> "'" + productType + "'").collect(Collectors.joining(","))).append(" ))");
        }
        if (filter.getEnvironmentType() != null) {
            sql.append(" and c.environment_type = '").append(filter.getEnvironmentType()).append("'");
        }
        if (filter.isNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(p.name)").append(filter.getSearchKey());
            sql.append(" or lower(p.barcode)").append(filter.getSearchKey());
            sql.append(" or lower(p.vat_barcode)").append(filter.getSearchKey());
            sql.append(") ");
        }
//        sql.append(" order by p.id, ");
        if (enabledSorting) {
            sql.append("order by ps.sort_order ");
        } else {
            sql.append("order by p.name");
        }

        Query query = entityManager
                .createNativeQuery(sql.toString(), "OrderProductLookUpMapper")
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());
        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
        }
        return query.getResultList();
    }

    @Override
    public ResultList<StockTransferProductListDTO> getListToStockTransfer(BaseFilter filter) {
        ResultList<StockTransferProductListDTO> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select distinct p.id, p.name, p.capacity as capacity, wp.qty qtyInWarehouse, wp.reservation_qty as reservationQty, p.barcode as barcode, p.box_barcode as boxBarcode, p.block_barcode as blockBarcode, p.has_mark as hasMark, p.product_type as productType, ");
        sql.append(" parent.id as parentId, ");
        sql.append(" parent.name as parentName, ");
        sql.append(" parent.barcode as parentBarcode, ");
        sql.append(" parent.vat_barcode as parentVatBarcode, ");
        sql.append(" p.weight_brutto as brutto, p.weight_netto as netto, p.price as price,");
        sql.append(" p.block_products as blockProducts");
        sql.append(getBaseListStockTransfer(filter));
        sql.append(" order by ");
        if (filter.isNotEmpty()) {
            sql.append(" p.name ");
        } else {
            sql.append(" p.id desc ");
        }

        StringBuilder countSql = new StringBuilder();
        countSql.append("select count(distinct p.id) \n");
        countSql.append(getBaseListStockTransfer(filter));
        Query countQuery = entityManager.createNativeQuery(countSql.toString());
        if (filter.isNotEmpty()) {
            countQuery.setParameter("searchKey", filter.getSearchValue());
        }
        long count = (long) (countQuery.getSingleResult());
        if (count > 0) {
            Query query = entityManager
                    .createNativeQuery(sql.toString(), "StockTransferProductMapper")
                    .setFirstResult(filter.getStart())
                    .setMaxResults(filter.getSize());
            if (filter.isNotEmpty()) {
                query.setParameter("searchKey", filter.getSearchValue());
            }

            List<StockTransferProductListDTO> result = query.getResultList();
            resultList.setList(result);
            resultList.setCount(count);
        }
        return resultList;
    }

    private StringBuilder getBaseListStockTransfer(BaseFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append(" from product p ");
        sql.append(" inner join warehouse_product wp on p.id = wp.product_id \n");
        sql.append(" inner join warehouse w on w.id = wp.warehouse_id \n");
        sql.append(" left join product parent on p.parent_id=parent.id \n");
        sql.append(" where p.deleted = false and p.price is not null and w.deleted = false");
        sql.append(" and p.company_id = ").append(SecurityUtils.getCurrentCompanyId());
        sql.append(" and w.id = ").append(filter.getWarehouseId());
        sql.append(" and p.status='").append(CommonStatus.ACTIVE.getCode()).append("'");
        if (!CollectionUtils.isEmpty(filter.getProductTypes())) {
            sql.append(" and p.product_type not in (").append(filter.getProductTypes().stream().map(productType -> "'" + productType + "'").collect(Collectors.joining(","))).append(" )");
        }
        if (filter.isNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(p.name)").append(filter.getSearchKey());
            sql.append(" or lower(p.barcode)").append(filter.getSearchKey());
            sql.append(" or lower(p.vat_barcode)").append(filter.getSearchKey());
            sql.append(") ");
        }
        return sql;
    }

    @Override
    public ResultList<WebKassaProductListDTO> getWebKassaProductList(ProductFilter filter) {
        ResultList<WebKassaProductListDTO> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append(" select p.id as id, ");
        sql.append(" p.name as name, ");
        sql.append(" p.favourite as favorite, ");
        sql.append(" p.price as price, ");
        sql.append(" p.barcode as barcode, ");
        sql.append(" p.vat_barcode as vatBarcode, ");
        sql.append(" p.vat as vat, ");
        sql.append(" p.has_mark as hasMark, ");
        sql.append(" p.category_id as categoryId, ");
        sql.append(" wp.qty as qtyInWarehouse, ");
        sql.append(" w.name as warehouseName, ");
        sql.append(" w.id as warehouseId, ");
        sql.append(" u.id as unitId, ");
        sql.append(" u.code as unitCode, ");
        String unitName;
        switch (SecurityUtils.getCurrentRequestLanguageEnum()) {
            case uz:
                unitName = "name_uz";
                break;
            case cyrillic:
                unitName = "name_cyrillic";
                break;
            default:
                unitName = "name_ru";
                break;
        }
        sql.append("u.").append(unitName).append(" as unitName, ");
        sql.append(" u.measure_id as measureId ");
        sql.append(getWebKassaProductBaseSql(filter));
        sql.append(" order by ");
        if (filter.isNotEmpty()) {
            sql.append(" p.name ");
        } else {
            sql.append(" p.id desc ");
        }
        Query query = entityManager.createNativeQuery(sql.toString(), "WebKassaProductListMapper");

        StringBuilder countSql = new StringBuilder();
        countSql.append("select count(p.id) \n");
        countSql.append(getWebKassaProductBaseSql(filter));

        Query countQuery = entityManager.createNativeQuery(countSql.toString());

        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
            countQuery.setParameter("searchKey", filter.getSearchValue());
        }
        countQuery.setParameter("categoryId", filter.getCategoryId());
        query.setParameter("categoryId", filter.getCategoryId());

        long count = (long) (countQuery.getSingleResult());
        if (count > 0) {
            query.setFirstResult(filter.getStart()).setMaxResults(filter.getSize());
            List<WebKassaProductListDTO> result = query.getResultList();
            resultList.setList(result);
            resultList.setCount(count);
        }
        return resultList;
    }

    public StringBuilder getWebKassaProductBaseSql(ProductFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append(" from product p ");
        sql.append(" inner join category c on p.category_id=c.id ");
        sql.append(" inner join branch b on p.branch_id=b.id and c.branch_id=b.id ");
        sql.append(" inner join warehouse_branch wb on b.id=wb.branch_id ");
        sql.append(" inner join warehouse w on wb.warehouse_id=w.id ");
        sql.append(" inner join unit u on p.unit_id=u.id ");
        sql.append(" inner join warehouse_product wp on wp.product_id=p.id and wp.warehouse_id=wb.warehouse_id");
        sql.append(" where p.deleted=false ");
        sql.append(" and b.deleted=false and c.id=:categoryId");
        if (filter.isNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(p.name)").append(filter.getSearchKey());
            sql.append(" or lower(p.barcode)").append(filter.getSearchKey());
            sql.append(" or lower(p.vat_barcode)").append(filter.getSearchKey());
            sql.append(") ");
        }
        return sql;
    }
}
