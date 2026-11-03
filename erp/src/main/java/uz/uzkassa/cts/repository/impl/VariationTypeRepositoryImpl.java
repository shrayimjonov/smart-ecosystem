package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.TypedQuery;
import uz.uzkassa.cts.domain.inventory.VariationType;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.custom.VariationTypeRepositoryCustom;

public class VariationTypeRepositoryImpl extends BaseRepositoryImpl implements VariationTypeRepositoryCustom {

    @Override
    public ResultList<VariationType> getResultList(BaseFilter filter) {
        ResultList<VariationType> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select vt from VariationType vt ");
        sql.append(" where vt.deleted = false ");

        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(vt.nameUzLatin) ").append(filter.getLikeSearch());
            sql.append(" or lower(vt.nameUzCyrillic) ").append(filter.getLikeSearch());
            sql.append(" or lower(vt.nameRu) ").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select vt", " select count(vt.id)");
        sql.append(" order by vt.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<VariationType> query = entityManager
                .createQuery(sql.toString(), VariationType.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

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
