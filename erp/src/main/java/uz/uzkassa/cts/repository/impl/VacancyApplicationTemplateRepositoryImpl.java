package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.TypedQuery;
import uz.uzkassa.cts.domain.hrms.VacancyApplicationTemplate;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.filter.VacancyApplicationTemplateFilter;
import uz.uzkassa.cts.repository.custom.VacancyApplicationTemplateRepositoryCustom;

@SuppressWarnings("unused")
public class VacancyApplicationTemplateRepositoryImpl extends BaseRepositoryImpl implements VacancyApplicationTemplateRepositoryCustom {

    @Override
    public ResultList<VacancyApplicationTemplate> getResultList(VacancyApplicationTemplateFilter filter) {
        ResultList<VacancyApplicationTemplate> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select vat from VacancyApplicationTemplate vat");
        sql.append(" join vat.position p");
        sql.append(" where vat.deleted = false");

        if (filter.getDepartmentId() != null) {
            sql.append(" and vat.departmentId = :departmentId");
        }
        if (filter.getBranchId() != null) {
            sql.append(" and vat.branchId = :branchId");
        }
        if (filter.getCompanyId() != null) {
            sql.append(" and vat.companyId = :companyId");
        }
        if (filter.getGroupId() != null) {
            sql.append(" and vat.groupId = :groupId");
        }
        if (filter.getPositionId() != null) {
            sql.append(" and vat.positionId = :positionId");
        }
        if (filter.getGradeId() != null) {
            sql.append(" and vat.gradeId = :gradeId");
        }
        if (filter.getFrom() != null) {
            sql.append(" and vat.createdDate >= :fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and vat.createdDate <= :toDate");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(vat.name)").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select vat", "select count(vat.id)");

        sql.append(" order by vat.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<VacancyApplicationTemplate> query = entityManager.createQuery(sql.toString(), VacancyApplicationTemplate.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (filter.getDepartmentId() != null) {
            query.setParameter("departmentId", filter.getDepartmentId());
            countQuery.setParameter("departmentId", filter.getDepartmentId());
        }
        if (filter.getBranchId() != null) {
            query.setParameter("branchId", filter.getBranchId());
            countQuery.setParameter("branchId", filter.getBranchId());
        }
        if (filter.getCompanyId() != null) {
            query.setParameter("companyId", filter.getCompanyId());
            countQuery.setParameter("companyId", filter.getCompanyId());
        }
        if (filter.getGroupId() != null) {
            query.setParameter("groupId", filter.getGroupId());
            countQuery.setParameter("groupId", filter.getGroupId());
        }
        if (filter.getPositionId() != null) {
            query.setParameter("positionId", filter.getPositionId());
            countQuery.setParameter("positionId", filter.getPositionId());
        }
        if (filter.getGradeId() != null) {
            query.setParameter("gradeId", filter.getGradeId());
            countQuery.setParameter("gradeId", filter.getGradeId());
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
