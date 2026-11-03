package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.hrms.Attendance;
import uz.uzkassa.cts.repository.custom.AttendanceRepositoryCustom;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends BaseRepository<Attendance, Long>, AttendanceRepositoryCustom {

    Optional<Attendance> findByEmployeeIdAndAttendanceDate(Long employeeId, LocalDate attendanceDate);
}
