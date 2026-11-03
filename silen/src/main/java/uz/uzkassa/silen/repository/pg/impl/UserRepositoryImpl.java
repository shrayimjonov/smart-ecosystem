package uz.uzkassa.silen.repository.pg.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import uz.uzkassa.silen.domain.User;
import uz.uzkassa.silen.dto.filter.UserFilter;
import uz.uzkassa.silen.repository.pg.UserRepositoryCustom;

public class UserRepositoryImpl implements UserRepositoryCustom {
    @PersistenceContext(unitName = "slavePU")
    private EntityManager entityManager;

    @Override
    public Page<User> findAllByFilter(UserFilter filter) {
        final boolean hasSearch = filter.checkSearchIsEmpty();
        final boolean hasSort = StringUtils.isNotEmpty(filter.getOrderBy());

        StringBuilder sql = new StringBuilder("select o from User o");
        if (filter.getAccounts() != null) {
            sql.append(" inner join o.organization org");
        }
        sql.append(" where o.deleted is not true");
        if (filter.getOrganizationId() != null) {
            sql.append(" and o.organizationId=:organizationId");
        }
        if (filter.getRole() != null) {
            sql.append(" and o.role=:role");
        }
        if (filter.getPinfl() != null) {
            sql.append(" and o.pinfl=:pinfl");
        }
        if (CollectionUtils.isNotEmpty(filter.getRoles())) {
            sql.append(" and o.role in (:roles)");
        }
        if (filter.getFrom() != null) {
            sql.append(" and o.lastModifiedDate>=:from");
        }
        if (filter.getTo() != null) {
            sql.append(" and o.lastModifiedDate<=:to");
        }
        if (filter.getLogin() != null) {
            sql.append(" and o.login=:login");
        }
        if (filter.isSupervisorNull()) {
            sql.append(" and o.supervisorId is null");
        }
        if (filter.getSupervisorId() != null) {
            sql.append(" and o.supervisorId=:supervisorId");
        }
        if (filter.getEmployee() != null) {
            sql.append(" and o.organizationId is null");
        }
        if (hasSearch) {
            sql.append(" and (lower(o.login) like :searchKey");
            sql.append(" or lower(o.firstName) like :searchKey");
            sql.append(" or lower(o.lastName) like :searchKey");
            sql.append(" or lower(o.phone) like :searchKey");
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

        TypedQuery<User> query = this.entityManager.createQuery(sql.toString(), User.class)
            .setFirstResult(filter.getStart())
            .setMaxResults(filter.getSize());
        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (filter.getOrganizationId() != null) {
            query.setParameter("organizationId", filter.getOrganizationId());
            countQuery.setParameter("organizationId", filter.getOrganizationId());
        }
        if (filter.getRole() != null) {
            query.setParameter("role", filter.getRole());
            countQuery.setParameter("role", filter.getRole());
        }
        if (filter.getPinfl() != null) {
            query.setParameter("pinfl", filter.getPinfl());
            countQuery.setParameter("pinfl", filter.getPinfl());
        }
        if (CollectionUtils.isNotEmpty(filter.getRoles())) {
            query.setParameter("roles", filter.getRoles());
            countQuery.setParameter("roles", filter.getRoles());
        }
        if (filter.getFrom() != null) {
            query.setParameter("from", filter.getFrom());
            countQuery.setParameter("from", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("to", filter.getTo());
            countQuery.setParameter("to", filter.getTo());
        }
        if (filter.getLogin() != null) {
            query.setParameter("login", filter.getLogin());
            countQuery.setParameter("login", filter.getLogin());
        }
        if (filter.getSupervisorId() != null) {
            query.setParameter("supervisorId", filter.getSupervisorId());
            countQuery.setParameter("supervisorId", filter.getSupervisorId());
        }
        if (hasSearch) {
            query.setParameter("searchKey", filter.getSearchForQuery());
            countQuery.setParameter("searchKey", filter.getSearchForQuery());
        }

        return new PageImpl<>(query.getResultList(), filter.getPageable(), countQuery.getSingleResult());
    }

}
