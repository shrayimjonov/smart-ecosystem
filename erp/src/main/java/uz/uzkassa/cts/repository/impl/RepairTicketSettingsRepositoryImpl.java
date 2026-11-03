package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.TypedQuery;
import uz.uzkassa.cts.domain.RepairTicketSettings;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.custom.RepairTicketSettingsRepositoryCustom;

public class RepairTicketSettingsRepositoryImpl extends BaseRepositoryImpl implements RepairTicketSettingsRepositoryCustom {

    @Override
    public ResultList<RepairTicketSettings> getResultList(BaseFilter filter) {
        ResultList<RepairTicketSettings> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append(" select rts from RepairTicketSettings rts ");
        sql.append(" join rts.product p ");
        sql.append(" where rts.deleted = false ");

        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(p.nameUzLatin) ").append(filter.getLikeSearch());
            sql.append(" or lower(p.nameUzCyrillic) ").append(filter.getLikeSearch());
            sql.append(" or lower(p.nameRu) ").append(filter.getLikeSearch());
            sql.append(" )");
        }
        if (filter.getFromStartOfDay() != null) {
            sql.append(" and rts.createdDate >=:from ");
        }
        if (filter.getToEndOfDay() != null) {
            sql.append(" and rts.createdDate <=:to ");
        }

        String countSql = sql.toString().replaceFirst("select rts", " select count(rts.id)");
        sql.append(" order by rts.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<RepairTicketSettings> query = entityManager
                .createQuery(sql.toString(), RepairTicketSettings.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (filter.isSearchNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchFor());
            countQuery.setParameter("searchKey", filter.getSearchFor());
        }
        if (filter.getFromStartOfDay() != null) {
            query.setParameter("from", filter.getFromStartOfDay());
            countQuery.setParameter("from", filter.getFromStartOfDay());
        }
        if (filter.getToEndOfDay() != null) {
            query.setParameter("to", filter.getToEndOfDay());
            countQuery.setParameter("to", filter.getToEndOfDay());
        }
        Long count = countQuery.getSingleResult();
        if (count > 0) {
            resultList.setList(query.getResultList());
            resultList.setCount(count);
        }
        return resultList;
    }
}
