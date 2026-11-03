package uz.uzkassa.horeca.repository.impl;

import jakarta.persistence.TypedQuery;
import uz.uzkassa.horeca.domain.hall.HallEmployee;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.filters.BaseFilter;
import uz.uzkassa.horeca.repository.HallEmployeeRepositoryCustom;

public class HallEmployeeRepositoryCustomImpl extends BaseRepositoryImpl implements HallEmployeeRepositoryCustom {

    @Override
    public ResultList<HallEmployee> getResultList(BaseFilter filter) {
        ResultList<HallEmployee> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append(" select d from HallEmployee d");
        sql.append(" where d.deleted = false");

        if (filter.getHallId() != null) {
            sql.append(" and d.hallId=:hallId");
        }

        String countSql = sql.toString().replaceFirst("select d", "select count(d.id)");

        sql.append(" order by d.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<HallEmployee> query = entityManager
                .createQuery(sql.toString(), HallEmployee.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (filter.getHallId() != null) {
            query.setParameter("hallId", filter.getHallId());
            countQuery.setParameter("hallId", filter.getHallId());
        }

        Long count = countQuery.getSingleResult();
        if (count > 0) {
            resultList.setList(query.getResultList());
            resultList.setCount(count);
        }
        return resultList;
    }
}
