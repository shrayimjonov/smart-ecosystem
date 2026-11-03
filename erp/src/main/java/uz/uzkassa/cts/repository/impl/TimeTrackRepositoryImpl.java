package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.domain.hrms.TimeTrack;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.report.TimeTrackReportDTO;
import uz.uzkassa.cts.enums.TimeTrackType;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.filter.TimeTrackReportFilter;
import uz.uzkassa.cts.repository.custom.TimeTrackRepositoryCustom;
import uz.uzkassa.cts.utils.QueryUtils;

import java.util.List;

@SuppressWarnings("unused")
public class TimeTrackRepositoryImpl extends BaseRepositoryImpl implements TimeTrackRepositoryCustom {

    @Override
    public ResultList<TimeTrack> getResultList(BaseFilter filter) {
        ResultList<TimeTrack> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select t from TimeTrack t");
        sql.append(" where t.deleted = false");

        if (filter.getEmployeeId() != null) {
            sql.append(" and t.employeeId = :employeeId");
        }
        if (filter.getCreatorId() != null) {
            sql.append(" and t.creatorId = :creatorId");
        }
        if (filter.getFrom() != null) {
            sql.append(" and t.createdDate >= :fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and t.createdDate <= :toDate");
        }
        if (StringUtils.isNotEmpty(filter.getType())) {
            TimeTrackType type = TimeTrackType.valueOf(filter.getType());
            if (TimeTrackType.IN == type) {
                sql.append(" and t.checkIn is not null");
            } else if (TimeTrackType.OUT == type) {
                sql.append(" and t.checkOut is not null");
            }
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(t.employee.firstName)").append(filter.getLikeSearch());
            sql.append(" or lower(t.employee.lastName)").append(filter.getLikeSearch());
            sql.append(" or lower(t.employee.login)").append(filter.getLikeSearch());
            sql.append(" or lower(t.employee.phone)").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select t", " select count(t.id)");
        sql.append(" order by t.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<TimeTrack> query = entityManager.createQuery(sql.toString(), TimeTrack.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (filter.getEmployeeId() != null) {
            query.setParameter("employeeId", filter.getEmployeeId());
            countQuery.setParameter("employeeId", filter.getEmployeeId());
        }
        if (filter.getCreatorId() != null) {
            query.setParameter("creatorId", filter.getCreatorId());
            countQuery.setParameter("creatorId", filter.getCreatorId());
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

    @Override
    public ResultList<TimeTrackReportDTO> getReport(TimeTrackReportFilter filter) {
        ResultList<TimeTrackReportDTO> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("""
                        select e.id                                                                                               as employeeId,
                               trim(concat(e.last_name, ' ', e.first_name, ' ', e.patronymic))                                    as employeeName,
                               d.id                                                                                               as departmentId,
                               d.name_ru                                                                                          as departmentName,
                               min(tt.check_in)                                                                                   as checkIn,
                               max(tt.check_out)                                                                                  as checkOut,
                               sum(extract(HOUR from tt.check_out::time) * 60 + extract(MINUTE from tt.check_out::time)) -
                               sum(extract(HOUR from tt.check_in::time) * 60 + extract(MINUTE from tt.check_in::time))            as actualTime,
                               ti.from_time                                                                                       as timeslotFromTime,
                               ti.to_time                                                                                         as timeslotToTime,
                               extract(HOUR from ti.to_time - ti.from_time) * 60 + extract(MINUTE from ti.to_time - ti.from_time) as timeslotTime,
                               extract(HOUR from min(tt.check_in)::time - ti.from_time) * 60 +
                               extract(MINUTE from min(tt.check_in)::time - ti.from_time)                                         as lessTime,
                               extract(HOUR from max(tt.check_out)::time - ti.to_time) * 60 +
                               extract(MINUTE from max(tt.check_out)::time - ti.to_time)                                          as overTime""")
                .append(getReportSql(filter))
                .append(" group by e.id, d.id, ti.from_time, ti.to_time")
                .append(" order by ").append(filter.getOrderBy().equals("id") ? "e.id" : filter.getOrderBy()).append(" ").append(filter.getSortOrder().getName())
                .append(QueryUtils.getOffsetLimit(filter.getPage(), filter.getSize()));

        List<TimeTrackReportDTO> list = entityManager.createNativeQuery(sql.toString(), "TimeTrackReportDTOMapper").getResultList();

        String countSql = "select count(distinct e.id)\n" + getReportSql(filter);
        Query countQuery = entityManager.createNativeQuery(countSql);

        long count = ((Long) countQuery.getSingleResult());
        if (count > 0) {
            resultList.setList(list);
            resultList.setCount(count);
        }
        return resultList;
    }

    private StringBuilder getReportSql(TimeTrackReportFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append(" from time_track tt\n");
        sql.append(" inner join jhi_user e on e.id = tt.employee_id\n");
        sql.append(" left join department d on d.id = e.department_id\n");
        sql.append(" left join timeslot_item ti on e.timeslot_id = ti.timeslot_id and ti.day = trim(to_char(date('").append(filter.getDate()).append("'), 'DAY'))\n");
        sql.append(" where (date(tt.check_in) = '").append(filter.getDate()).append("'");
        sql.append(" or date(tt.check_out) = '").append(filter.getDate()).append("')");
        if (filter.getCompanyId() != null) {
            sql.append(" and e.company_id = ").append(filter.getCompanyId());
        }
        if (filter.getBranchId() != null) {
            sql.append(" and e.branch_id = ").append(filter.getBranchId());
        }
        if (filter.getDepartmentId() != null) {
            sql.append(" and e.department_id = ").append(filter.getDepartmentId());
        }
        return sql;
    }
}
