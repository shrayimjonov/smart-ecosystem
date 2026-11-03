package uz.uzkassa.smartpossupply.repository;

import uz.uzkassa.smartpossupply.domain.trip.Trip;
import uz.uzkassa.smartpossupply.enums.TripStatus;

import java.time.LocalDate;
import java.util.List;

public interface TripRepository extends BaseRepository<Trip, Long> {

    List<Trip> findAllByExpeditorIdAndStatusInAndTripDateOrderById(Long expeditorId, List<TripStatus> statuses, LocalDate tripDate);

}
