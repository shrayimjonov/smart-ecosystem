package uz.uzkassa.horeca.repository;

import org.springframework.data.domain.Page;
import uz.uzkassa.horeca.domain.order.Reservation;
import uz.uzkassa.horeca.filters.ReservationFilter;

public interface ReservationRepositoryCustom {
    Page<Reservation> getResultList(ReservationFilter filter);
}
