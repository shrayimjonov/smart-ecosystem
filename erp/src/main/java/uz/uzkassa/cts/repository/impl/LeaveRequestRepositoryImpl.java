package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import uz.uzkassa.cts.domain.hrms.LeaveRequest;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.custom.LeaveRequestRepositoryCustom;

@Component
public class LeaveRequestRepositoryImpl extends BaseRepositoryImpl implements LeaveRequestRepositoryCustom {
    @Override
    public ResultList<LeaveRequest> getResultList(BaseFilter filter) {
        ResultList<LeaveRequest> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select lr from LeaveRequest lr ");
        sql.append(" where lr.deleted = false ");

        if (StringUtils.hasText(filter.getStatus())) {
            sql.append(" and lr.status =:status ");
        }
        if (filter.getCompanyId() != null) {
            sql.append(" and lr.companyId =:companyId ");
        }
        if (filter.getFrom() != null && filter.getTo() != null) {
            sql.append(" and (");
            sql.append(" (lr.fromDate >= :fromDate and lr.fromDate <= :toDate) ");
            sql.append(" or (lr.toDate >= :fromDate and lr.toDate <= :toDate) ");
            sql.append(") ");
        } else if (filter.getFrom() != null) {
            sql.append(" and (lr.fromDate >= :fromDate or lr.toDate >= :fromDate) ");
        } else if (filter.getTo() != null) {
            sql.append(" and (lr.fromDate <= :toDate or lr.toDate <= :toDate) ");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(lr.leaveRequestNumber) ").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select lr", " select count(lr.id)");
        sql.append(" order by lr.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<LeaveRequest> query = entityManager
                .createQuery(sql.toString(), LeaveRequest.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (StringUtils.hasText(filter.getStatus())) {
            query.setParameter("status", CommonStatus.get(filter.getStatus()));
            countQuery.setParameter("status", CommonStatus.get(filter.getStatus()));
        }
        if (filter.getCompanyId() != null) {
            query.setParameter("companyId", filter.getCompanyId());
            countQuery.setParameter("companyId", filter.getCompanyId());
        }
        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFromStartOfDay());
            countQuery.setParameter("fromDate", filter.getFromStartOfDay());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getToEndOfDay());
            countQuery.setParameter("toDate", filter.getToEndOfDay());
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
