package uz.uzkassa.cts.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.hrms.TimeTrack;
import uz.uzkassa.cts.repository.custom.TimeTrackRepositoryCustom;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TimeTrackRepository extends BaseRepository<TimeTrack, Long>, TimeTrackRepositoryCustom {

    @Query(value = "SELECT tt FROM TimeTrack tt WHERE tt.deleted IS FALSE AND (DATE(tt.checkIn) = :date OR DATE(tt.checkOut) = :date) AND tt.employeeId = :employeeId ORDER BY tt.id")
    List<TimeTrack> findAllByDateAndEmployeeIdOrderById(@Param("date") LocalDate date, @Param("employeeId") Long employeeId);

    Optional<TimeTrack> findByIdAndEmployeeIdAndDeletedIsFalse(Long id, Long employeeId);
}