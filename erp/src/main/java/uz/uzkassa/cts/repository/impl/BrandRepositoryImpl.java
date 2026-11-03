package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.TypedQuery;
import uz.uzkassa.cts.domain.inventory.Brand;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.custom.BrandRepositoryCustom;

@SuppressWarnings("unused")
public class BrandRepositoryImpl extends BaseRepositoryImpl implements BrandRepositoryCustom {

    @Override
    public ResultList<Brand> getResultList(BaseFilter filter) {
        ResultList<Brand> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select b from Brand b ");
        sql.append(" where b.deleted = false ");
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(b.name) ").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select b", " select count(b.id)");
        sql.append(" order by b.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<Brand> query = entityManager
                .createQuery(sql.toString(), Brand.class)
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
