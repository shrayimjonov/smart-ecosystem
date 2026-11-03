package uz.uzkassa.silen.repository.pg.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import uz.uzkassa.silen.domain.Attributes;
import uz.uzkassa.silen.dto.AttributeValueDTO;
import uz.uzkassa.silen.dto.filter.AttributesFilter;
import uz.uzkassa.silen.enumeration.ProductType;
import uz.uzkassa.silen.repository.pg.AttributesRepositoryCustom;

import java.util.List;

public class AttributesRepositoryImpl implements AttributesRepositoryCustom {
    @PersistenceContext(unitName = "slavePU")
    private EntityManager entityManager;

    @Override
    public Page<Attributes> findAllByFilter(AttributesFilter filter) {
        final boolean hasSearch = filter.checkSearchIsEmpty();
        final boolean hasSort = StringUtils.isNotEmpty(filter.getOrderBy());
        StringBuilder sql = new StringBuilder("select o.* from attributes o");
        if (filter.getProductType() != null) {
            sql.append(" left join attributes_product_type apt on o.id = apt.attribute_id");
        }
        sql.append(" where o.deleted is not true");

        if (filter.getOrganizationId() != null) {
            sql.append(" and o.organization_id=:organizationId");
        }
        if (filter.getProductType() != null) {
            sql.append(" and apt.product_types='").append(filter.getProductType().name()).append("'");
        }
        if (filter.getFrom() != null) {
            sql.append(" and o.last_modified_date>=:from");
        }
        if (filter.getTo() != null) {
            sql.append(" and o.last_modified_date<=:to");
        }

        if (hasSearch) {
            sql.append(" and (lower(o.name) like :searchKey");
            sql.append(")");
        }
        String countSql = sql.toString().replace("select o.*", "select count(o.id)");

            sql.append(" order by");
        if (hasSort) {
            sql.append(" o.").append(filter.getOrderBy());
        } else {
            sql.append(" o.last_modified_date");
        }
        sql.append(" ").append(filter.getSortOrder().getName());
        Query query = this.entityManager.createNativeQuery(sql.toString(), Attributes.class)
            .setFirstResult(filter.getStart())
            .setMaxResults(filter.getSize());
        Query countQuery = entityManager.createNativeQuery(countSql);

        if (filter.getOrganizationId() != null) {
            query.setParameter("organizationId", filter.getOrganizationId());
            countQuery.setParameter("organizationId", filter.getOrganizationId());
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
        return new PageImpl<Attributes>(query.getResultList(), filter.getPageable(), (Long) countQuery.getSingleResult());
    }

    @Override
    public List<AttributeValueDTO> getAttributesValue(String productId) {
        StringBuilder sql = new StringBuilder("select a.name, a.prefix, pa.attribute_value as value, a.postfix");
        sql.append(" from attributes a");
        sql.append(" left join product_attributes pa on a.id = pa.attribute_id");
        sql.append(" where a.visible is true and pa.product_id =:productId order by sorter asc");

        Query query = this.entityManager.createNativeQuery(sql.toString(), "AttributeValueMapper")
            .setParameter("productId", productId);

        return query.getResultList();
    }

    @Override
    public int getMaxSortOrderValue() {
        Query query = this.entityManager.createNativeQuery("select max(sorter) from attributes");
        return (int) query.getSingleResult();
    }

    @Override
    public List<Attributes> getAttributesByType(ProductType productType) {
        String sql = String.format("SELECT a.* FROM attributes a INNER JOIN attributes_product_type apt ON a.id = apt.attribute_id WHERE apt.product_types = '%s' ORDER BY sorter ASC", productType.name());
        Query query = this.entityManager.createNativeQuery(sql, Attributes.class);
        return query.getResultList();
    }
}
