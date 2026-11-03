package uz.uzkassa.smartpossupply.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.domain.auth.User;
import uz.uzkassa.smartpossupply.domain.base.SimpleEntity;
import uz.uzkassa.smartpossupply.domain.stocktransfer.StockTransfer;
import uz.uzkassa.smartpossupply.domain.trip.Trip;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.trip.TripSwapDTO;
import uz.uzkassa.smartpossupply.enums.Role;
import uz.uzkassa.smartpossupply.enums.TripStatus;
import uz.uzkassa.smartpossupply.enums.UserStatus;
import uz.uzkassa.smartpossupply.repository.ExpeditorRepository;
import uz.uzkassa.smartpossupply.repository.StockTransferRepository;
import uz.uzkassa.smartpossupply.repository.TripRepository;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;
import uz.uzkassa.smartpossupply.web.rest.errors.BadRequestException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TripService extends BaseService {
    TripRepository tripRepository;
    ExpeditorRepository expeditorRepository;
    StockTransferRepository stockTransferRepository;

    @Transactional
    public void create() {
        List<User> expeditors = expeditorRepository.findAllByRoleAndStatusAndDeletedIsFalse(Role.ROLE_EXPEDITOR, UserStatus.ACTIVE);
        List<Trip> trips = new ArrayList<>();
        for (User expeditor : expeditors) {
            for (int i = 1; i <= 20; i++) {
                Trip trip = new Trip();
                trip.setExpeditorId(expeditor.getId());
                trip.setTripNumber("№ " + i);
                trip.setStatus(TripStatus.NEW);
                trip.setTripDate(LocalDate.now());
                trips.add(trip);
            }
        }
        tripRepository.saveAll(trips);
    }

    public List<CommonDTO> getItems(Long expeditorId, List<TripStatus> statuses) {
        return tripRepository.findAllByExpeditorIdAndStatusInAndTripDateOrderById(expeditorId, statuses, LocalDate.now())
            .stream()
            .map(SimpleEntity::toCommonDTO)
            .toList();
    }

    @Transactional
    public void swap(TripSwapDTO tripSwapDTO) {
        Long companyId = SecurityUtils.getCompanyId();

        StockTransfer fromStockTransfer = stockTransferRepository.findByIdAndCompanyIdAndTripIdAndDeletedIsFalse(tripSwapDTO.getFromStockTransferId(), companyId, tripSwapDTO.getFromTripId())
                .orElseThrow(validationService.entityNotFoundExceptionThrow(StockTransfer.class.getSimpleName(), "id", tripSwapDTO.getFromStockTransferId()));

        StockTransfer toStockTransfer = stockTransferRepository.findByIdAndCompanyIdAndTripIdAndDeletedIsFalse(tripSwapDTO.getToStockTransferId(), companyId, tripSwapDTO.getToTripId())
                .orElseThrow(validationService.entityNotFoundExceptionThrow(StockTransfer.class.getSimpleName(), "id", tripSwapDTO.getToStockTransferId()));

        if(!Objects.equals(fromStockTransfer.getExpeditorId(), toStockTransfer.getExpeditorId())) {
            throw new BadRequestException("Вы не можете обменять рейсы, так как экспедиторы не совпадают.");
        }

        if (TripStatus.IN_PROGRESS != fromStockTransfer.getTrip().getStatus() || TripStatus.IN_PROGRESS != toStockTransfer.getTrip().getStatus()) {
            throw new BadRequestException("Trips are not in progress");
        }

        fromStockTransfer.setTripId(tripSwapDTO.getToTripId());
        toStockTransfer.setTripId(tripSwapDTO.getFromTripId());
        stockTransferRepository.saveAll(List.of(fromStockTransfer, toStockTransfer));
    }

    @Transactional
    public void updateStatus(Trip trip, TripStatus status) {
        trip.setStatus(status);
        tripRepository.save(trip);
    }
}
