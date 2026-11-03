package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.domain.hrms.Vacancy;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.enums.VacancyStatus;
import uz.uzkassa.cts.filter.VacancyFilter;
import uz.uzkassa.cts.repository.custom.VacancyRepositoryCustom;

@SuppressWarnings("unused")
public class VacancyRepositoryImpl extends BaseRepositoryImpl implements VacancyRepositoryCustom {

    @Override
    public ResultList<Vacancy> getResultList(VacancyFilter filter) {
        ResultList<Vacancy> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select v from Vacancy v ");
        sql.append(" join v.position p ");
        sql.append(" where v.deleted = false ");

        if (filter.getCompanyId() != null) {
            sql.append(" and v.companyId =:companyId ");
        }
        if (filter.getBranchId() != null) {
            sql.append(" and v.branchId =:branchId ");
        }
        if (filter.getDepartmentId() != null) {
            sql.append(" and v.departmentId =:departmentId ");
        }
        if (filter.getPositionId() != null) {
            sql.append(" and v.positionId =:positionId ");
        }
        if (filter.getGradeId() != null) {
            sql.append(" and v.gradeId =:gradeId ");
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and v.status =:status ");
        }
        if (filter.getFrom() != null) {
            sql.append(" and v.createdDate >= :fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and v.createdDate <= :toDate");
        }
        if (filter.getHrId() != null) {
            sql.append(" and v.hrManagerId =:hrId ");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(v.name) ").append(filter.getLikeSearch());
            sql.append(" or lower(v.vacancyNumber) ").append(filter.getLikeSearch());
            sql.append(" or lower(p.nameUzLatin) ").append(filter.getLikeSearch());
            sql.append(" or lower(p.nameUzCyrillic) ").append(filter.getLikeSearch());
            sql.append(" or lower(p.nameRu) ").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select v", "select count(v.id)");

        sql.append(" order by v.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<Vacancy> query = entityManager.createQuery(sql.toString(), Vacancy.class)
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
            countQuery.setParameter("positionId", filter.getParentId());
        }
        if (filter.getGradeId() != null) {
            query.setParameter("gradeId", filter.getGradeId());
            countQuery.setParameter("gradeId", filter.getGradeId());
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("status", VacancyStatus.valueOf(filter.getStatus()));
            countQuery.setParameter("status", VacancyStatus.valueOf(filter.getStatus()));
        }
        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFromStartOfDay());
            countQuery.setParameter("fromDate", filter.getFromStartOfDay());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getToEndOfDay());
            countQuery.setParameter("toDate", filter.getToEndOfDay());
        }
        if (filter.getHrId() != null) {
            query.setParameter("hrId", filter.getHrId());
            countQuery.setParameter("hrId", filter.getHrId());
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
