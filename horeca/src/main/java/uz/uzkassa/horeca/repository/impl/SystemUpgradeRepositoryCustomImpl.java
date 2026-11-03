package uz.uzkassa.horeca.repository.impl;

import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.horeca.domain.SystemUpgrade;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.dto.factura.PlatformType;
import uz.uzkassa.horeca.filters.SystemUpgradeFilter;
import uz.uzkassa.horeca.repository.SystemUpgradeRepositoryCustom;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 03.10.2024
 */
public class SystemUpgradeRepositoryCustomImpl extends BaseRepositoryImpl implements SystemUpgradeRepositoryCustom {

    @Override
    public ResultList<SystemUpgrade> getResultList(SystemUpgradeFilter filter) {
        ResultList<SystemUpgrade> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select su from SystemUpgrade su ");
        sql.append(" where 1=1 ");

        if (filter.getAvailable() != null) {
            sql.append(" and su.available=:available ");
        }
        if (StringUtils.isNotEmpty(filter.getType())) {
            sql.append(" and su.platformType=:platformType");
        }
        if (filter.getFrom() != null) {
            sql.append(" and su.releaseDate>=:fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and su.releaseDate<=:toDate");
        }
        if (filter.hasSearch()) {
            sql.append(" and(");
            sql.append(" lower(su.version)").append(filter.getLikeSearchKey());
            sql.append(" )");
        }
        String countSql = sql.toString().replaceFirst("select su", "select count(su.id)");
        sql.append(" order by su.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<SystemUpgrade> query = entityManager
                .createQuery(sql.toString(), SystemUpgrade.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (filter.getAvailable() != null) {
            query.setParameter("available", filter.getAvailable());
            countQuery.setParameter("available", filter.getAvailable());
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("platformType", PlatformType.valueOf(filter.getType()));
            countQuery.setParameter("platformType", PlatformType.valueOf(filter.getType()));
        }
        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFrom());
            countQuery.setParameter("fromDate", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getTo());
            countQuery.setParameter("toDate", filter.getTo());
        }
        if (filter.hasSearch()) {
            query.setParameter("searchKey", filter.getSearchForQuery());
            countQuery.setParameter("searchKey", filter.getSearchForQuery());
        }
        Long count = countQuery.getSingleResult();
        if (count > 0) {
            resultList.setList(query.getResultList());
            resultList.setCount(count);
        }
        return resultList;
    }
}
