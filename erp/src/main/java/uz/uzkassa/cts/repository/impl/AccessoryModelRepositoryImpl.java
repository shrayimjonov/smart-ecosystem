package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.domain.AccessoryModel;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.custom.AccessoryModelRepositoryCustom;


@SuppressWarnings("unused")
public class AccessoryModelRepositoryImpl extends BaseRepositoryImpl implements AccessoryModelRepositoryCustom {

    @Override
    public ResultList<AccessoryModel> getResultList(BaseFilter filter) {
        ResultList<AccessoryModel> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select a from AccessoryModel a ");
        sql.append(" where a.deleted = false ");

        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and a.status = :status ");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(a.name)").append(filter.getLikeSearch());
            sql.append(" or lower(a.description)").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select a", " select count(a.id)");
        sql.append(" order by a.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<AccessoryModel> query = entityManager
                .createQuery(sql.toString(), AccessoryModel.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (filter.isSearchNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchFor());
            countQuery.setParameter("searchKey", filter.getSearchFor());
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("status", CommonStatus.valueOf(filter.getStatus()));
            countQuery.setParameter("status", CommonStatus.valueOf(filter.getStatus()));
        }
        Long count = countQuery.getSingleResult();
        if (count > 0) {
            resultList.setList(query.getResultList());
            resultList.setCount(count);
        }
        return resultList;
    }
}
