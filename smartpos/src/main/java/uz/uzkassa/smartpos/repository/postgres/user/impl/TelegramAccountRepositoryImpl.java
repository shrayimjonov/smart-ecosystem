package uz.uzkassa.smartpos.repository.postgres.user.impl;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import uz.uzkassa.smartpos.domain.postgres.user.TelegramAccount;
import uz.uzkassa.smartpos.dto.filter.TelegramAccountFilter;
import uz.uzkassa.smartpos.repository.postgres.user.TelegramAccountRepositoryCustom;

@SuppressWarnings("unused")
public class TelegramAccountRepositoryImpl implements TelegramAccountRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<TelegramAccount> findAllByFilter(TelegramAccountFilter filter) {
        final boolean hasSearch = StringUtils.isNotEmpty(filter.getSearch());
        final boolean hasSort = StringUtils.isNotEmpty(filter.getOrderBy());

        StringBuilder sql = new StringBuilder("select t from TelegramAccount t where 1=1 ");
        sql.append(" and t.name is not null ");
        if (filter.getCompanyId() != null) {
            sql.append(" and t.companyId=:companyId");
        }
        if (filter.getBranchId() != null) {
            sql.append(" and t.user.branchId=:branchId");
        }
        if (filter.getUserId() != null) {
            sql.append(" and t.userId=:userId");
        }
        if (filter.getConfirmed() != null) {
            sql.append(" and t.confirmed = :confirmed");
        }
        if (filter.getEnabled() != null) {
            sql.append(" and t.enabled = :enabled");
        }
        if (hasSearch) {
            sql.append(" and (lower(t.phone) like :searchKey");
            sql.append(" or lower(t.name) like :searchKey)");
        }
        String countSql = sql.toString().replaceFirst("select t", "select count(t)");
        sql.append(" order by");
        if (hasSort) {
            sql.append(" t.").append(filter.getOrderBy());
        } else {
            sql.append(" t.id");
        }
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<TelegramAccount> query = entityManager.createQuery(sql.toString(), TelegramAccount.class)
            .setFirstResult(filter.getStart()).setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (filter.getCompanyId() != null) {
            query.setParameter("companyId", filter.getCompanyId());
            countQuery.setParameter("companyId", filter.getCompanyId());
        }
        if (filter.getBranchId() != null) {
            query.setParameter("branchId", filter.getBranchId());
            countQuery.setParameter("branchId", filter.getBranchId());
        }
        if (filter.getUserId() != null) {
            query.setParameter("userId", filter.getUserId());
            countQuery.setParameter("userId", filter.getUserId());
        }
        if (filter.getConfirmed() != null) {
            query.setParameter("confirmed", filter.getConfirmed());
            countQuery.setParameter("confirmed", filter.getConfirmed());
        }
        if (filter.getEnabled() != null) {
            query.setParameter("enabled", filter.getEnabled());
            countQuery.setParameter("enabled", filter.getEnabled());
        }

        if (hasSearch) {
            query.setParameter("searchKey", filter.getSearchForQuery());
            countQuery.setParameter("searchKey", filter.getSearchForQuery());
        }
        return new PageImpl<>(query.getResultList(), filter.getPageable(), countQuery.getSingleResult());
    }
}
