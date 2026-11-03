package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.TypedQuery;
import uz.uzkassa.cts.domain.hrms.Attendance;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.filter.AttendanceFilter;
import uz.uzkassa.cts.repository.custom.AttendanceRepositoryCustom;

import java.time.LocalDate;

/**
 * Created date time: 29.01.2025 14:15
 */

@SuppressWarnings("unused")
public class AttendanceRepositoryImpl extends BaseRepositoryImpl implements AttendanceRepositoryCustom {

    @Override
    public ResultList<Attendance> getResultList(AttendanceFilter filter) {
        ResultList<Attendance> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select a from Attendance a");
        sql.append(" join a.employee e");
        sql.append(" where a.attendanceDate = :date");
        sql.append(" and e.companyId = :companyId");

        if (filter.getEmployeeId() != null) {
            sql.append(" and e.id = :employeeId");
        }
        if (filter.getDepartmentId() != null) {
            sql.append(" and e.departmentId = :departmentId");
        }
        if (filter.getPositionId() != null) {
            sql.append(" and e.positionId = :positionId");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(e.firstName)").append(filter.getLikeSearch());
            sql.append(" or lower(e.lastName)").append(filter.getLikeSearch());
            sql.append(" or lower(e.patronymic)").append(filter.getLikeSearch());
            sql.append(" or lower(e.phone)").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select a", " select count(a.id)");
        sql.append(" order by a.employeeId");
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<Attendance> query = entityManager
                .createQuery(sql.toString(), Attendance.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        query.setParameter("date", LocalDate.of(filter.getYear(), filter.getMonth(), 1));
        countQuery.setParameter("date", LocalDate.of(filter.getYear(), filter.getMonth(), 1));

        query.setParameter("companyId", getCompanyId(filter.getCompanyId()));
        countQuery.setParameter("companyId", getCompanyId(filter.getCompanyId()));

        if (filter.getEmployeeId() != null) {
            query.setParameter("employeeId", filter.getEmployeeId());
            countQuery.setParameter("employeeId", filter.getEmployeeId());
        }
        if (filter.getDepartmentId() != null) {
            query.setParameter("departmentId", filter.getDepartmentId());
            countQuery.setParameter("departmentId", filter.getDepartmentId());
        }
        if (filter.getPositionId() != null) {
            query.setParameter("positionId", filter.getPositionId());
            countQuery.setParameter("positionId", filter.getPositionId());
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
