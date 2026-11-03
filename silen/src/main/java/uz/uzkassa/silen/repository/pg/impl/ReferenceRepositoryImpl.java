package uz.uzkassa.silen.repository.pg.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import uz.uzkassa.silen.domain.Reference;
import uz.uzkassa.silen.dto.SelectItem;
import uz.uzkassa.silen.dto.filter.ReferenceFilter;
import uz.uzkassa.silen.repository.pg.ReferenceRepositoryCustom;

import java.util.List;

public class ReferenceRepositoryImpl implements ReferenceRepositoryCustom {
    @PersistenceContext(unitName = "slavePU")
    private EntityManager entityManager;

    @Override
    public Page<Reference> findAllByFilter(ReferenceFilter filter) {
        final boolean hasSearch = filter.checkSearchIsEmpty();
        final boolean hasSort = StringUtils.isNotEmpty(filter.getOrderBy());

        StringBuilder sql = new StringBuilder("select o from Reference o");
        sql.append(" where o.deleted is not true");
        if (filter.getParentCode() != null) {
            sql.append(" and o.parentId=:parentId");
        } else {
            sql.append(" and o.parentId is null");
        }
        if (hasSearch) {
            sql.append(" and (lower(o.name) like :searchKey");
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

        TypedQuery<Reference> query = this.entityManager.createQuery(sql.toString(), Reference.class)
            .setFirstResult(filter.getStart())
            .setMaxResults(filter.getSize());
        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (filter.getParentCode() != null) {
            query.setParameter("parentId", filter.getParentCode());
            countQuery.setParameter("parentId", filter.getParentCode());
        }
        if (hasSearch) {
            query.setParameter("searchKey", filter.getSearchForQuery());
            countQuery.setParameter("searchKey", filter.getSearchForQuery());
        }

        return new PageImpl<>(query.getResultList(), filter.getPageable(), countQuery.getSingleResult());
    }

    @Override
    public List<SelectItem> getItems(ReferenceFilter filter) {
        final boolean hasSearch = filter.checkSearchIsEmpty();

        StringBuilder sql = new StringBuilder("select o.id as id, o.name as name, o.code as description from reference o");
        if(filter.getParentCode() != null){
            sql.append(" inner join reference p on p.code=o.parent_id");
        }
        sql.append(" where o.deleted is not true");
        if (filter.getParentCode() != null) {
            sql.append(" and p.code=:parentCode");
        }
        if (hasSearch) {
            sql.append(" and lower(o.name) like :searchKey");
        }
        sql.append(" order by o.name");

        Query query = this.entityManager.createNativeQuery(sql.toString(), "SelectItemDescriptionMapper")
            .setFirstResult(filter.getStart())
            .setMaxResults(filter.getSize());

        if (filter.getParentCode() != null) {
            query.setParameter("parentCode", filter.getParentCode());
        }
        if (hasSearch) {
            query.setParameter("searchKey", filter.getSearchForQuery());
        }

        return query.getResultList();
    }
}
