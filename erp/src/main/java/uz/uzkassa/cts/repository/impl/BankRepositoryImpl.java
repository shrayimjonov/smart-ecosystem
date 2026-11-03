package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.domain.Bank;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.custom.BankRepositoryCustom;

@SuppressWarnings("unused")
public class BankRepositoryImpl extends BaseRepositoryImpl implements BankRepositoryCustom {

    @Override
    public ResultList<Bank> getResultList(BaseFilter filter) {
        ResultList<Bank> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select b from Bank b ");
        sql.append(" where b.deleted = false ");
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and b.status =:status ");
        }

        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(b.nameUzLatin) ").append(filter.getLikeSearch());
            sql.append(" or lower(b.nameUzCyrillic) ").append(filter.getLikeSearch());
            sql.append(" or lower(b.nameRu) ").append(filter.getLikeSearch());
            sql.append(" or lower(b.code)").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select b", " select count(b.id)");
        sql.append(" order by b.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<Bank> query = entityManager
                .createQuery(sql.toString(), Bank.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("status", CommonStatus.valueOf(filter.getStatus()));
            countQuery.setParameter("status", CommonStatus.valueOf(filter.getStatus()));
        }
        if (filter.isSearchNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchFor());
            countQuery.setParameter("searchKey", filter.getSearchFor());
        }
        Long count = countQuery.getSingleResult();
        if (count > 0) {
            resultList.setList(query.getResultList());
            resultList.setCount(count);
        }
        return resultList;
    }
}
