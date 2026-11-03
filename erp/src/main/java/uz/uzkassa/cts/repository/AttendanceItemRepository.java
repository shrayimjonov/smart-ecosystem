package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.hrms.AttendanceItem;

import java.util.Optional;

@Repository
public interface AttendanceItemRepository extends BaseRepository<AttendanceItem, Long> {

    Optional<AttendanceItem> findByAttendanceIdAndAttendanceDay(Long attendanceId, Integer attendanceDay);
}
