package uz.uzkassa.horeca.repository.impl;

import jakarta.persistence.TypedQuery;
import uz.uzkassa.horeca.domain.Terminal;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.filters.BaseFilter;
import uz.uzkassa.horeca.repository.TerminalRepositoryCustom;


public class TerminalRepositoryImpl extends BaseRepositoryImpl implements TerminalRepositoryCustom {

    @Override
    public ResultList<Terminal> getResultList(BaseFilter filter) {
        ResultList<Terminal> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select t ");
        sql.append(getBaseSql(filter));

        String countSql = sql.toString().replaceFirst("select t", "select count(t.id)");
        sql.append(" order by t.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<Terminal> query = entityManager
                .createQuery(sql.toString(), Terminal.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (filter.getCompanyId() != null) {
            query.setParameter("companyId", filter.getCompanyId());
            countQuery.setParameter("companyId", filter.getCompanyId());
        }
        if (filter.getBranchId() != null) {
            query.setParameter("branchId", filter.getBranchId());
            countQuery.setParameter("branchId", filter.getBranchId());
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

    private StringBuilder getBaseSql(BaseFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append(" from Terminal t ");
        sql.append(" join t.branch b ");
        sql.append(" join b.company c ");
        sql.append(" where t.deleted = false ");
        if (filter.getCompanyId() != null) {
            sql.append(" and c.id =:companyId");
        }
        if (filter.getBranchId() != null) {
            sql.append(" and t.branchId =:branchId");
        }
        if (filter.hasSearch()) {
            sql.append(" and (");
            sql.append(" lower(t.serialNumber)").append(filter.getLikeSearchKey());
            sql.append(" or lower(c.tin)").append(filter.getLikeSearchKey());
            sql.append(")");
        }
        return sql;
    }
}
