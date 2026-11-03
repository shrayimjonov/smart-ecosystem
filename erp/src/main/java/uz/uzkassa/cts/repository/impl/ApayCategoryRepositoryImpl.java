package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.domain.apay.APayCategory;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.filter.ApayCategoryFilter;
import uz.uzkassa.cts.repository.custom.ApayCategoryRepositoryCustom;

@SuppressWarnings("unused")
public class ApayCategoryRepositoryImpl extends BaseRepositoryImpl implements ApayCategoryRepositoryCustom {

    @Override
    public ResultList<APayCategory> getResultList(ApayCategoryFilter filter) {
        ResultList<APayCategory> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select ap from APayCategory ap ");
        sql.append(" where ap.deleted = false ");
        if (filter.getOnlyParents() != null && filter.getOnlyParents()) {
            sql.append(" and ap.parentId is null ");
        } else if (StringUtils.isNotEmpty(filter.getApayParentId())) {
            sql.append(" and ap.parentId =:parentId ");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(ap.nameUzLatin) ").append(filter.getLikeSearch());
            sql.append(" or lower(ap.nameUzCyrillic) ").append(filter.getLikeSearch());
            sql.append(" or lower(ap.nameRu) ").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select ap", " select count(ap.id)");
        sql.append(" order by ap.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<APayCategory> query = entityManager
                .createQuery(sql.toString(), APayCategory.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (StringUtils.isNotEmpty(filter.getApayParentId())) {
            query.setParameter("parentId", filter.getApayParentId());
            countQuery.setParameter("parentId", filter.getApayParentId());
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
