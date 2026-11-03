package uz.uzkassa.horeca.repository.impl;

import jakarta.persistence.TypedQuery;
import uz.uzkassa.horeca.domain.Bank;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.filters.BaseFilter;
import uz.uzkassa.horeca.repository.BankRepositoryCustom;

public class BankRepositoryImpl extends BaseRepositoryImpl implements BankRepositoryCustom {

    @Override
    public ResultList<Bank> getResultList(BaseFilter filter) {
        ResultList<Bank> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select b from Bank b \n");
        sql.append(" where b.deleted = false ");

        if (filter.hasSearch()) {
            sql.append(" and (");
            sql.append(" lower(b.nameUz) ").append(filter.getLikeSearchKey());
            sql.append(" or lower(b.nameRu) ").append(filter.getLikeSearchKey());
            sql.append(" or lower(b.nameCyrillic) ").append(filter.getLikeSearchKey());
            sql.append(" or lower(b.mfo)").append(filter.getLikeSearchKey());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select b", " select count(b.id)");
        sql.append(" order by b.nameRu");

        TypedQuery<Bank> query = entityManager
            .createQuery(sql.toString(), Bank.class)
            .setFirstResult(filter.getStart())
            .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (filter.hasSearch()) {
            query.setParameter("searchKey", filter.getSearchForQuery());
            countQuery.setParameter("searchKey", filter.getSearchForQuery());
        }
        Long count = countQuery.getSingleResult();
        if (count > 0) {
            resultList.setList(query.getResultList());
            resultList.setCount(count);
        }
        return resultList;
    }
}
