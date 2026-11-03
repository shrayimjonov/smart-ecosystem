package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.domain.hrms.VacancyApplication;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.enums.VacancyApplicationStatus;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.custom.VacancyApplicationRepositoryCustom;

@SuppressWarnings("unused")
public class VacancyApplicationRepositoryImpl extends BaseRepositoryImpl implements VacancyApplicationRepositoryCustom {

    @Override
    public ResultList<VacancyApplication> getResultList(BaseFilter filter) {
        ResultList<VacancyApplication> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select va from VacancyApplication va ");
        sql.append(" join va.position p ");
        sql.append(" where va.deleted = false ");

        if (filter.getCompanyId() != null) {
            sql.append(" and va.companyId =:companyId ");
        }
        if (filter.getBranchId() != null) {
            sql.append(" and va.branchId =:branchId ");
        }
        if (filter.getDepartmentId() != null) {
            sql.append(" and va.departmentId =:departmentId ");
        }
        if (filter.getPositionId() != null) {
            sql.append(" and p.id =:positionId ");
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and va.status =:status ");
        }
        if (filter.getFrom() != null) {
            sql.append(" and va.createdDate >= :fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and va.createdDate <= :toDate");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(va.name)").append(filter.getLikeSearch());
            sql.append(" or lower(va.vacancyApplicationNumber)").append(filter.getLikeSearch());
            sql.append(" or lower(p.nameUzLatin)").append(filter.getLikeSearch());
            sql.append(" or lower(p.nameUzCyrillic)").append(filter.getLikeSearch());
            sql.append(" or lower(p.nameRu)").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select va", "select count(va.id)");

        sql.append(" order by va.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<VacancyApplication> query = entityManager.createQuery(sql.toString(), VacancyApplication.class)
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
        if (filter.getDepartmentId() != null) {
            query.setParameter("departmentId", filter.getDepartmentId());
            countQuery.setParameter("departmentId", filter.getDepartmentId());
        }
        if (filter.getPositionId() != null) {
            query.setParameter("positionId", filter.getPositionId());
            countQuery.setParameter("positionId", filter.getPositionId());
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("status", VacancyApplicationStatus.valueOf(filter.getStatus()));
            countQuery.setParameter("status", VacancyApplicationStatus.valueOf(filter.getStatus()));
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
