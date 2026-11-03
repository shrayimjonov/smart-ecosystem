package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.Bank;
import uz.uzkassa.smartpossupply.filters.BankFilter;
import uz.uzkassa.smartpossupply.repository.BankRepositoryCustom;

import java.util.List;

@Repository
public class BankRepositoryImpl extends BaseRepositoryImpl implements BankRepositoryCustom {

    @Override
    public List<Bank> lookUp(BankFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT b FROM Bank b ");
        sql.append(" WHERE b.deleted = false ");
        if (filter.getParentId() != null) {
            sql.append(" AND b.parentId =:parentId ");
        } else if (filter.isFetchOnlyParent()) {
            sql.append(" AND b.parentId is null ");
        }

        if (filter.isNotEmpty()) {
            sql.append(" AND (");
            sql.append(" LOWER(b.nameUz)").append(filter.getSearchKey());
            sql.append(" OR LOWER(b.nameRu)").append(filter.getSearchKey());
            sql.append(" OR LOWER(b.nameCyrillic)").append(filter.getSearchKey());
            sql.append(" OR LOWER(b.mfo)").append(filter.getSearchKey());
            sql.append(")");
        }

        sql.append(" ORDER BY b.nameRu");

        TypedQuery<Bank> query = entityManager
            .createQuery(sql.toString(), Bank.class)
            .setFirstResult(filter.getStart())
            .setMaxResults(filter.getSize());

        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
        }
        if (filter.getParentId() != null) {
            query.setParameter("parentId", filter.getParentId());
        }

        return query.getResultList();
    }
}
