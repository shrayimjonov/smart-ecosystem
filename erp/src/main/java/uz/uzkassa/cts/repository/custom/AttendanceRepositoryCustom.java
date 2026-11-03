package uz.uzkassa.cts.repository.custom;

import uz.uzkassa.cts.domain.hrms.Attendance;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.filter.AttendanceFilter;

public interface AttendanceRepositoryCustom {

    ResultList<Attendance> getResultList(AttendanceFilter filter);
}
