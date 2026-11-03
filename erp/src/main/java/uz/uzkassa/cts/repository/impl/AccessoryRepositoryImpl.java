package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.domain.Accessory;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.custom.AccessoryRepositoryCustom;


@SuppressWarnings("unused")
public class AccessoryRepositoryImpl extends BaseRepositoryImpl implements AccessoryRepositoryCustom {


    @Override
    public ResultList<Accessory> getResultList(BaseFilter filter) {
        ResultList<Accessory> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select a from Accessory a ");
        sql.append(" where a.deleted = false ");

        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and a.status = :status ");
        }

        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(a.code)").append(filter.getLikeSearch());
            sql.append(" or lower(a.packageCode)").append(filter.getLikeSearch());
            sql.append(" or lower(a.packageName)").append(filter.getLikeSearch());
            sql.append(" or lower(a.catalogName)").append(filter.getLikeSearch());
            sql.append(" or lower(a.catalogCode)").append(filter.getLikeSearch());
            sql.append(" or lower(a.description)").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select a", " select count(a.id)");
        sql.append(" order by a.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<Accessory> query = entityManager
                .createQuery(sql.toString(), Accessory.class)
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
