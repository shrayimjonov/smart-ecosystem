package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.TypedQuery;
import uz.uzkassa.cts.domain.inventory.VariationOption;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.filter.VariationOptionFilter;
import uz.uzkassa.cts.repository.custom.VariationOptionRepositoryCustom;

public class VariationOptionRepositoryImpl extends BaseRepositoryImpl implements VariationOptionRepositoryCustom {

    @Override
    public ResultList<VariationOption> getResultList(VariationOptionFilter filter) {
        ResultList<VariationOption> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select va from VariationOption va ");
        sql.append(" where va.deleted = false ");
        if (filter.getVariationTypeId() != null) {
            sql.append(" and va.variationTypeId =:variationTypeId ");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(va.option) ").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select va", " select count(va.id)");
        sql.append(" order by va.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<VariationOption> query = entityManager
                .createQuery(sql.toString(), VariationOption.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (filter.getVariationTypeId() != null) {
            query.setParameter("variationTypeId", filter.getVariationTypeId());
            countQuery.setParameter("variationTypeId", filter.getVariationTypeId());
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
