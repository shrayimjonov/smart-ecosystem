package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.TypedQuery;
import uz.uzkassa.cts.domain.History;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.filter.HistoryListFilter;
import uz.uzkassa.cts.repository.custom.HistoryRepositoryCustom;

import java.util.List;

@SuppressWarnings("unused")
public class HistoryRepositoryImpl extends BaseRepositoryImpl implements HistoryRepositoryCustom {

    @Override
    public ResultList<History> getListResult(HistoryListFilter filter) {
        ResultList<History> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select h ");
        sql.append(" from History h \n");
        sql.append(" where h.fm = :fm ");
        sql.append(" and h.serialNumber = :serialNumber ");

        String countSql = sql.toString().replaceFirst("select h", "select count(h.id)");

        sql.append(" order by h.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<History> query = entityManager.createQuery(sql.toString(), History.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class)
                .setMaxResults(1);

        query.setParameter("fm", filter.isFm());
        countQuery.setParameter("fm", filter.isFm());

        query.setParameter("serialNumber", filter.getSerialNumber());
        countQuery.setParameter("serialNumber", filter.getSerialNumber());

        List<Long> listCount = countQuery.getResultList();

        long count = listCount.isEmpty() ? 0L : listCount.getFirst();
        if (count > 0) {
            resultList.setList(query.getResultList());
            resultList.setCount(count);
        }

        return resultList;
    }
}
