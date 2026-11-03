package uz.uzkassa.horeca.repository.impl;

import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.horeca.domain.hall.Hall;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.enums.CommonStatus;
import uz.uzkassa.horeca.filters.BaseFilter;
import uz.uzkassa.horeca.repository.HallRepositoryCustom;

public class HallRepositoryImpl extends BaseRepositoryImpl implements HallRepositoryCustom {

    @Override
    public ResultList<Hall> getResultList(BaseFilter filter) {
        ResultList<Hall> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append(" select h from Hall h ");
        sql.append(" where h.deleted = false");
        sql.append(" and h.branchId =:branchId ");
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and h.status =:status ");
        }

        if (filter.hasSearch()) {
            sql.append(" and ( ");
            sql.append(" lower(h.name)").append(filter.getLikeSearchKey());
            sql.append(") ");
        }

        String countSql = sql.toString().replaceFirst("select h", "select count(h.id)");

        sql.append(" order by h.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<Hall> query = entityManager
            .createQuery(sql.toString(), Hall.class)
            .setFirstResult(filter.getStart())
            .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        query.setParameter("branchId", filter.getBranchId());
        countQuery.setParameter("branchId", filter.getBranchId());

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
