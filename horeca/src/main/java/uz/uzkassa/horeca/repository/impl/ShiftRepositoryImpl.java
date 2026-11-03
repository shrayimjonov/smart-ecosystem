package uz.uzkassa.horeca.repository.impl;

import jakarta.persistence.TypedQuery;
import uz.uzkassa.horeca.domain.shift.Shift;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.filters.BaseFilter;
import uz.uzkassa.horeca.repository.ShiftRepositoryCustom;

public class ShiftRepositoryImpl extends BaseRepositoryImpl implements ShiftRepositoryCustom {

    @Override
    public ResultList<Shift> getResultList(BaseFilter filter) {
        ResultList<Shift> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append(" select s from Shift s ");
        sql.append(" join s.branch b");
        sql.append(" where b.companyId=:companyId ");

        if (filter.getBranchId() != null) {
            sql.append(" and b.id=:branchId ");
        }

        if (filter.getFrom() != null) {
            sql.append(" and s.createdDate>=:fromDate ");
        }
        if (filter.getTo() != null) {
            sql.append(" and s.createdDate<=:toDate ");
        }
        if (filter.hasSearch()) {
            sql.append(" and (");
            sql.append(" lower(s.shiftNumber) ").append(filter.getLikeSearchKey());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select s", "select count(s.id)");

        sql.append(" order by s.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<Shift> query = entityManager
                .createQuery(sql.toString(), Shift.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        query.setParameter("companyId", filter.getCompanyId());
        countQuery.setParameter("companyId", filter.getCompanyId());

        if (filter.getBranchId() != null) {
            query.setParameter("branchId", filter.getBranchId());
            countQuery.setParameter("branchId", filter.getBranchId());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getTo());
            countQuery.setParameter("toDate", filter.getTo());
        }
        if (filter.hasSearch()) {
            query.setParameter("searchKey", filter.getSearchForQuery());
            countQuery.setParameter("searchKey", filter.getSearchForQuery());
        }

        Long listCount = countQuery.getSingleResult();
        if (listCount > 0) {
            resultList.setList(query.getResultList());
            resultList.setCount(listCount);
        }
        return resultList;
    }
}
