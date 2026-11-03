package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.Holiday;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.enums.StockStatus;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.custom.HolidayRepositoryCustom;

@Component
public class HolidayRepositoryImpl extends BaseRepositoryImpl implements HolidayRepositoryCustom {

    @Override
    public ResultList<Holiday> getResultList(BaseFilter filter) {
        ResultList<Holiday> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select h from Holiday h ");
        sql.append(" where h.deleted = false ");

        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and h.status =:status ");
        }
        if (filter.getCompanyId() != null) {
            sql.append(" and h.companyId =:companyId ");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(h.name) ").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select h", " select count(h.id)");
        sql.append(" order by h.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<Holiday> query = entityManager
                .createQuery(sql.toString(), Holiday.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("status", StockStatus.get(filter.getStatus()));
            countQuery.setParameter("status", StockStatus.get(filter.getStatus()));
        }
        if (filter.getCompanyId() != null) {
            query.setParameter("companyId", filter.getCompanyId());
            countQuery.setParameter("companyId", filter.getCompanyId());
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
