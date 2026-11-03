package uz.uzkassa.silen.repository.pg.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import uz.uzkassa.silen.domain.Product;
import uz.uzkassa.silen.dto.ProductSelectCountItem;
import uz.uzkassa.silen.dto.SelectItem;
import uz.uzkassa.silen.dto.filter.ProductFilter;
import uz.uzkassa.silen.repository.pg.ProductRepositoryCustom;

import java.util.List;

public class ProductRepositoryImpl implements ProductRepositoryCustom {
    @PersistenceContext(unitName = "slavePU")
    private EntityManager entityManager;

    @Override
    public Page<Product> findAllByFilter(ProductFilter filter) {
        final boolean hasSearch = filter.checkSearchIsEmpty();
        final boolean hasSort = StringUtils.isNotEmpty(filter.getOrderBy());

        StringBuilder sql = new StringBuilder("select o from Product o");
        sql.append(" where o.deleted is not true");
        if (filter.getOrganizationId() != null) {
            sql.append(" and o.organizationId=:organizationId");
        }
        if (filter.getProductType() != null) {
            sql.append(" and o.type=:productType");
        }
        if (CollectionUtils.isNotEmpty(filter.getProductTypes())) {
            sql.append(" and o.type in (:productTypes)");
        }
        if (filter.getStatus() != null) {
            sql.append(" and o.status=:status");
        }
        if (filter.getFrom() != null) {
            sql.append(" and o.lastModifiedDate>=:from");
        }
        if (filter.getTo() != null) {
            sql.append(" and o.lastModifiedDate<=:to");
        }
        if (hasSearch) {
            sql.append(" and (lower(o.name) like :searchKey");
            sql.append(" or lower(o.shortName) like :searchKey");
            sql.append(" or lower(o.barcode) like :searchKey");
            sql.append(" or lower(o.parentBarcode) like :searchKey");
            sql.append(")");
        }
        String countSql = sql.toString().replaceFirst("select o", "select count(o.id)");

        sql.append(" order by");
        if (hasSort) {
            sql.append(" o.").append(filter.getOrderBy());
        } else {
            sql.append(" o.lastModifiedDate");
        }
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<Product> query = this.entityManager.createQuery(sql.toString(), Product.class)
            .setFirstResult(filter.getStart())
            .setMaxResults(filter.getSize());
        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (filter.getOrganizationId() != null) {
            query.setParameter("organizationId", filter.getOrganizationId());
            countQuery.setParameter("organizationId", filter.getOrganizationId());
        }
        if (filter.getProductType() != null) {
            query.setParameter("productType", filter.getProductType());
            countQuery.setParameter("productType", filter.getProductType());
        }
        if (CollectionUtils.isNotEmpty(filter.getProductTypes())) {
            query.setParameter("productTypes", filter.getProductTypes());
            countQuery.setParameter("productTypes", filter.getProductTypes());
        }
        if (filter.getStatus() != null) {
            query.setParameter("status", filter.getStatus());
            countQuery.setParameter("status", filter.getStatus());
        }
        if (filter.getFrom() != null) {
            query.setParameter("from", filter.getFrom());
            countQuery.setParameter("from", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("to", filter.getTo());
            countQuery.setParameter("to", filter.getTo());
        }
        if (hasSearch) {
            query.setParameter("searchKey", filter.getSearchForQuery());
            countQuery.setParameter("searchKey", filter.getSearchForQuery());
        }

        return new PageImpl<>(query.getResultList(), filter.getPageable(), countQuery.getSingleResult());
    }

    @Override
    public List<SelectItem> getItems(ProductFilter filter) {
        return null;
    }

    @Override
    public List<SelectItem> getChildren(String productId, String searchKey) {
        final boolean hasSearch = StringUtils.isNotEmpty(searchKey);
        StringBuilder sql = new StringBuilder("select p.id as id, p.name as name from product_childs pc");
        sql.append(" inner join product p on pc.child_id=p.id");
        sql.append(" where p.deleted is not true");
        sql.append(" and pc.product_id=:productId");
        if (hasSearch) {
            sql.append(" and (lower(p.name) like :searchKey");
            sql.append(" or lower(p.short_name) like :searchKey");
            sql.append(" or lower(p.barcode) like :searchKey");
            sql.append(" or lower(p.parentBarcode) like :searchKey");
            sql.append(")");
        }
        Query query = this.entityManager.createNativeQuery(sql.toString(), "SelectItemMapper")
            .setMaxResults(10)
            .setParameter("productId", productId);
        if (hasSearch) {
            query.setParameter("searchKey", StringUtils.join("%", searchKey));
        }
        return query.getResultList();
    }

    @Override
    public List<ProductSelectCountItem> getCounts(ProductFilter filter) {

        StringBuilder sql = new StringBuilder("select type, count(id) as counter from product");
        sql.append(" where deleted=false");
        sql.append(" and organization_id = :orgId");
        sql.append(" group by type");
        sql.append(" order by type");
        Query query = this.entityManager.createNativeQuery(sql.toString(), "ProductSelectCountItemMapper")
            .setParameter("orgId", filter.getOrganizationId());
        return (List<ProductSelectCountItem>) query.getResultList();

    }

    @Override
    public Page<SelectItem> selectCatalogCodeItems(ProductFilter filter) {
        List<SelectItem> itemList = selectCatalogCodeItemsList(filter);

        String countSql = "select count(distinct catalogCode) from Product where deleted=false and catalogCode is not null";

        TypedQuery<Long> countQuery = this.entityManager.createQuery(countSql, Long.class);

        return new PageImpl<>(itemList, filter.getPageable(), countQuery.getSingleResult());
    }

    public List<SelectItem> selectCatalogCodeItemsList(ProductFilter filter) {
        StringBuilder sql = new StringBuilder("select distinct catalog_code as id,'' as name from product");
        sql.append(" where deleted=false");
        sql.append(" and catalog_code is not null");
        sql.append(" limit ");
        sql.append(filter.getSize());
        sql.append(" offset ");
        sql.append(filter.getStart());

        Query query = this.entityManager.createNativeQuery(sql.toString(), "SelectItemMapper");
        return query.getResultList();
    }
}
