package uz.uzkassa.horeca.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.uzkassa.horeca.domain.order.Reservation;
import uz.uzkassa.horeca.enums.ReservationStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends BaseRepository<Reservation, Long>, ReservationRepositoryCustom {
    List<Reservation> findAllByTableIdAndDeletedIsFalseAndStatusInOrderByFromDate(Long tableId, List<ReservationStatus> status);

    @Query("select coalesce(max (r.incrementNumber),0)from Reservation r where r.branchId=:branchId")
    Long getMaxIncrementNumber(@Param("branchId") Long branchId);

    List<Reservation> findAllByStatusAndDeletedIsFalseAndFromDateIsAfter(ReservationStatus status, LocalDateTime fromDate);

    List<Reservation> findAllByStatusInAndDeletedIsFalseAndFromDateIsBefore(List<ReservationStatus> status, LocalDateTime fromDate);

    boolean existsByTableIdAndDeletedIsFalseAndStatusIn(Long tableId, List<ReservationStatus> status);

    boolean existsByTableIdAndDeletedIsFalse(Long tableId);
}
