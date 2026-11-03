package uz.uzkassa.cts.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.hrms.LeaveRequest;
import uz.uzkassa.cts.repository.custom.LeaveRequestRepositoryCustom;

import java.time.LocalDate;

@Repository
public interface LeaveRequestRepository extends BaseRepository<LeaveRequest, Long>, LeaveRequestRepositoryCustom {

    @Query("SELECT coalesce(max(lr.incrementNumber), 0) FROM LeaveRequest lr WHERE lr.companyId=:companyId")
    Long getMaxIncrementNumber(@Param("companyId") Long companyId);

    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.deleted IS FALSE AND DATE(lr.fromDate) <= :date AND DATE(lr.toDate) >= :date AND lr.creatorId = :employeeId")
    Long getLeaveRequestIdByDateAndCreatorId(@Param("date") LocalDate date, @Param("employeeId") Long employeeId);

    @Query("SELECT lr.duration FROM LeaveRequest lr WHERE lr.creator.id = :creatorId")
    Long getDurationByCreatorId(@Param("creatorId") Long creatorId);

}
