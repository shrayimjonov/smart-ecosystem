package uz.uzkassa.horeca.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.horeca.domain.auth.User;
import uz.uzkassa.horeca.domain.base.SimpleEntity;
import uz.uzkassa.horeca.domain.hall.HallItem;
import uz.uzkassa.horeca.domain.order.Order;
import uz.uzkassa.horeca.domain.order.Reservation;
import uz.uzkassa.horeca.dto.base.CheckStatusResponseDTO;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.base.UpdateStatusDTO;
import uz.uzkassa.horeca.dto.payload.OrderEventPayload;
import uz.uzkassa.horeca.dto.reservation.*;
import uz.uzkassa.horeca.enums.*;
import uz.uzkassa.horeca.filters.ReservationFilter;
import uz.uzkassa.horeca.interfaces.CrudAdvanced;
import uz.uzkassa.horeca.interfaces.Numbering;
import uz.uzkassa.horeca.rabbitmq.producer.RabbitMqOrderProducer;
import uz.uzkassa.horeca.repository.HallItemRepository;
import uz.uzkassa.horeca.repository.OrderItemRepository;
import uz.uzkassa.horeca.repository.OrderRepository;
import uz.uzkassa.horeca.repository.ReservationRepository;
import uz.uzkassa.horeca.utils.DateUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ReservationService
        extends BaseService
        implements CrudAdvanced<Long, ReservationDTO, ReservationDetailDTO, ReservationListDTO, ReservationFilter, ReservationUpdateStatusDTO, CommonDTO>,
        Numbering {

    ReservationRepository reservationRepository;
    CustomerService customerService;
    HallItemService hallItemService;
    HallItemRepository hallItemRepository;
    RabbitMqOrderProducer rabbitMqOrderProducer;
    OrderRepository orderRepository;
    OrderItemRepository orderItemRepository;

    @Override
    public HashMap<String, String> getNumberingMap(Long branchId) {
        Long incrementNumber = reservationRepository.getMaxIncrementNumber(branchId) + 1L;
        String reservationNumber = Prefix.RESERVATION
                .getName()
                .concat(DateUtils.formatYearLastTwoDigit(LocalDate.now()))
                .concat("-")
                .concat(COMMON_NUMBERING_FORMAT.format(incrementNumber));

        HashMap<String, String> numberMap = new HashMap<>();
        numberMap.put(INCREMENT_NUMBER, String.valueOf(incrementNumber));
        numberMap.put(NUMBER, reservationNumber);
        return numberMap;
    }

    @Override
    public void validate(ReservationDTO reservationDTO) {
        if (reservationDTO.getTableId() == null) {
            throw badRequestExceptionThrow("Table is required").get();
        }
        if (StringUtils.isEmpty(reservationDTO.getName())) {
            throw badRequestExceptionThrow("Name is required").get();
        }
        if (StringUtils.isEmpty(reservationDTO.getPhone())) {
            throw badRequestExceptionThrow("Phone is required").get();
        }
        if (reservationDTO.getNumberOfGuest() == null) {
            throw badRequestExceptionThrow("Number of guest is required").get();
        }
        if (StringUtils.isEmpty(reservationDTO.getGuestName())) {
            throw badRequestExceptionThrow("Guest name is required").get();
        }
        if (reservationDTO.getFromDate() == null) {
            throw badRequestExceptionThrow("From date is required").get();
        }
    }

    @Transactional
    @Override
    public Long create(ReservationDTO reservationDTO) {
        this.validate(reservationDTO);
        HallItem hallItem = hallItemRepository
                .findById(reservationDTO.getTableId())
                .orElseThrow(notFoundExceptionThrow(HallItem.class.getSimpleName(), "id", reservationDTO.getTableId()));
        Long branchId = hallItem.getHall().getBranchId();
        HashMap<String, String> numberingMap = this.getNumberingMap(branchId);

        Reservation reservation = new Reservation();
        reservation.setIncrementNumber(Long.valueOf(numberingMap.get(INCREMENT_NUMBER)));
        reservation.setReservationNumber(numberingMap.get(NUMBER));
        reservation.setTableId(reservationDTO.getTableId());
        reservation.setBranchId(branchId);
        if (StringUtils.isNotEmpty(reservationDTO.getPhone()) && StringUtils.isNotEmpty(reservationDTO.getGuestName())) {
            User client = customerService.createEntity(new CustomerDTO(reservationDTO.getName(), reservationDTO.getPhone(), branchId));
            reservation.setClientId(client.getId());
            reservation.setClient(client);
        }
        reservation.setGuestName(reservationDTO.getGuestName());
        reservation.setNumberOfGuest(reservationDTO.getNumberOfGuest());
        reservation.setFromDate(reservationDTO.getFromDate());
        reservation.setToDate(reservationDTO.getToDate());
        reservation.setComment(reservationDTO.getComment());
        if (reservation.getFromDate().minusHours(1).isBefore(LocalDateTime.now())) {
            if (HallItemStatus.FREE.equals(hallItem.getStatus())) {
                hallItemService.updateStatus(hallItem.getId(), new UpdateStatusDTO(HallItemStatus.BOOKED.getCode()));
            }
        }
        reservationRepository.save(reservation);
        return reservation.getId();
    }

    @Override
    public ReservationDetailDTO get(Long id) {
        return reservationRepository
                .findById(id)
                .map(reservation -> {
                    ReservationDetailDTO reservationDetailDTO = new ReservationDetailDTO();
                    reservationDetailDTO.setId(reservation.getId());
                    reservationDetailDTO.setReservationNumber(reservation.getReservationNumber());
                    if (reservation.getTable() != null) {
                        MobileTableDetailWithMergedTablesDTO tableDetailDTO = new MobileTableDetailWithMergedTablesDTO();
                        tableDetailDTO.setId(reservation.getTableId());
                        tableDetailDTO.setName(reservation.getTable().getName());
                        tableDetailDTO.setNumberOfGuest(reservation.getTable().getCapacity());
                        tableDetailDTO.setStatus(reservation.getTable().getStatus());
                        if (reservation.getTable().getHall() != null) {
                            tableDetailDTO.setHall(reservation.getTable().getHall().toCommonDTO());
                        }
                        reservation.getTable().getMergedTables().forEach(table -> tableDetailDTO.addTable(table.toCommonDTO(), table.getCapacity()));
                        reservationDetailDTO.setTable(tableDetailDTO);
                    }
                    reservationDetailDTO.setFromDate(reservation.getFromDate());
                    reservationDetailDTO.setToDate(reservation.getToDate());
                    reservationDetailDTO.setGuestName(reservation.getGuestName());
                    if (reservation.getClientId() != null) {
                        reservationDetailDTO.setClient(
                                new CustomerDetailDTO(
                                        reservation.clientCommonDTO(),
                                        reservation.getClient().getPhone()
                                )
                        );
                    }
                    reservationDetailDTO.setNumberOfGuest(reservation.getNumberOfGuest());
                    if (reservation.getOrder() != null && !OrderStatus.CANCELLED.equals(reservation.getOrder().getStatus())) {
                        reservationDetailDTO.setOrder(reservation.getOrder().toCommonDTO());
                    }
                    reservationDetailDTO.setComment(reservation.getComment());
                    reservationDetailDTO.setLastModifiedDate(reservation.getUpdatedDate());
                    reservationDetailDTO.setStatus(reservation.getStatus());
                    reservationDetailDTO.setCreator(reservation.getCreator().toCommonDTO());
                    return reservationDetailDTO;
                })
                .orElseThrow(notFoundExceptionThrow(Reservation.class.getSimpleName(), "id", id));
    }

    @Transactional
    @Override
    public Long update(Long id, ReservationDTO reservationDTO) {
        reservationDTO.setId(id);
        this.validate(reservationDTO);
        return reservationRepository
                .findById(id)
                .map(reservation -> {
                    reservation.setTableId(reservationDTO.getTableId());
                    reservation.setNumberOfGuest(reservationDTO.getNumberOfGuest());
                    reservation.setGuestName(reservationDTO.getGuestName());
                    reservation.setFromDate(reservationDTO.getFromDate());
                    reservation.setToDate(reservationDTO.getToDate());
                    reservation.setComment(reservationDTO.getComment());
                    if (StringUtils.isNotEmpty(reservationDTO.getPhone()) && StringUtils.isNotEmpty(reservationDTO.getGuestName())) {
                        User client = customerService.createEntity(new CustomerDTO(reservationDTO.getName(), reservationDTO.getPhone(), reservation.getBranchId()));
                        reservation.setClientId(client.getId());
                        reservation.setClient(client);
                    }
                    if (reservation.getFromDate().minusHours(1).isBefore(LocalDateTime.now())) {
                        if (HallItemStatus.FREE.equals(reservation.getTable().getStatus())) {
                            hallItemService.updateStatus(reservation.getTableId(), new UpdateStatusDTO(HallItemStatus.BOOKED.getCode()));
                        }
                    } /*else {
                        if (HallItemStatus.BOOKED.equals(reservation.getTable().getStatus())) {
                            hallItemService.updateStatus(reservation.getTableId(), new UpdateStatusDTO(HallItemStatus.FREE.getCode()));
                        }
                    }*/
                    reservationRepository.save(reservation);
                    return reservation.getId();
                })
                .orElseThrow(notFoundExceptionThrow(Reservation.class.getSimpleName(), "id", id));
    }

    @Override
    public Page<ReservationListDTO> getList(ReservationFilter filter) {
        return reservationRepository.getResultList(filter)
                .map(reservation -> {
                    ReservationListDTO reservationListDTO = new ReservationListDTO();
                    reservationListDTO.setReservationNumber(reservation.getReservationNumber());
                    reservationListDTO.setId(reservation.getId());
                    if (reservation.getClientId() != null) {
                        reservationListDTO.setClient(
                                new CustomerDetailDTO(reservation.clientCommonDTO(),
                                        reservation.getClient().getPhone()
                                )
                        );
                    }
                    reservationListDTO.setTable(reservation.getTable().toCommonDTO());
                    if (reservation.getTable().getHall() != null) {
                        reservationListDTO.setHall(reservation.getTable().getHall().toCommonDTO());
                    }
                    reservationListDTO.setNumberOfGuest(reservation.getNumberOfGuest());
                    reservationListDTO.setToDate(reservation.getToDate());
                    reservationListDTO.setFromDate(reservation.getFromDate());
                    if (reservation.getOrderId() != null) {
                        reservationListDTO.setOrder(reservation.getOrder().toCommonDTO());
                    }
                    reservationListDTO.setStatus(reservation.getStatus());
                    reservationListDTO.setTableStatus(reservation.getTable().getStatus());
                    return reservationListDTO;
                });
    }

    @Override
    @Transactional
    public void delete(Long id) {
        reservationRepository
                .findById(id)
                .map(reservation -> {
                    if (reservation.getTableId() != null) {
                        hallItemService.updateStatus(reservation.getTableId(), new UpdateStatusDTO(HallItemStatus.FREE.getCode()));
                    }
                    reservation.setDeleted(true);
                    reservationRepository.save(reservation);
                    return reservation;
                });
    }

    @Override
    public List<CommonDTO> lookUp(ReservationFilter reservationFilter) {
        return reservationRepository
                .getResultList(reservationFilter)
                .map(SimpleEntity::toCommonDTO).getContent();
    }

    @Override
    @Transactional
    public void updateStatus(Long id, ReservationUpdateStatusDTO updateStatusDTO) {
        ReservationStatus status = updateStatusDTO.getStatus();
        reservationRepository
                .findById(id)
                .map(reservation -> {
                    reservation.setStatus(updateStatusDTO.getStatus());
                    reservation.setReason(updateStatusDTO.getReason());
                    reservation.setOtherReason(updateStatusDTO.getOtherReason());
                    reservationRepository.save(reservation);
                    if (ReservationStatus.CANCELLED.equals(status)) {
                        HallItem hallItem = reservation.getTable();
                        if (HallItemStatus.BOOKED.equals(hallItem.getStatus())) {
                            hallItem.setStatus(HallItemStatus.ACTIVE);
                            hallItem.setWithOrder(false);
                            hallItemRepository.save(hallItem);
                        }
                        if (reservation.getOrderId() != null) {
                            Order order = reservation.getOrder();
                            order.setStatus(OrderStatus.ACTIVE);
                            order.setReason(updateStatusDTO.getReason());
                            order.setOtherReason(updateStatusDTO.getOtherReason());
                            orderRepository.save(order);
                            order.getItems().stream()
                                    .filter(orderItem -> !OrderItemStatus.CANCELLED.equals(orderItem.getStatus()))
                                    .forEach(reservationOrderItem -> {
                                        reservationOrderItem.setStatus(OrderItemStatus.CANCELLED);
                                        orderItemRepository.save(reservationOrderItem);
                                    });
                        }
                    }
                    if (ReservationStatus.ACTIVE.equals(status)) {
                        HallItem hallItem = reservation.getTable();
                        if (HallItemStatus.BOOKED.equals(hallItem.getStatus())) {
                            hallItem.setStatus(HallItemStatus.ACTIVE);
                            hallItem.setWithOrder(true);
                            hallItemRepository.save(hallItem);
                        }
                        if (reservation.getOrderId() != null) {
                            Order order = reservation.getOrder();
                            if (EnumSet.of(OrderStatus.BOOKED, OrderStatus.PENDING).contains(order.getStatus())) {
                                order.setStatus(OrderStatus.ACTIVE);
                                orderRepository.save(order);
                                order.getItems().forEach(item -> {
                                    item.setStatus(OrderItemStatus.NEW);
                                    orderItemRepository.save(item);
                                });
                                afterCommitExecutor.execute(() -> rabbitMqOrderProducer.sendEvent(new OrderEventPayload(reservation.getOrderId(), OrderEventType.ORDER_CREATE)));
                            }
                        }
                    }
                    return reservation;
                })
                .orElseThrow(notFoundExceptionThrow(Reservation.class.getSimpleName(), "id", id));
    }

    @Transactional
    public CheckStatusResponseDTO transfer(Long id, Long tableId, LocalDateTime lastModifiedDate) {
        return reservationRepository
                .findById(id)
                .map(reservation -> {
                    if (!reservation.getUpdatedDate().equals(lastModifiedDate)) {
                        return new CheckStatusResponseDTO(false, 1);
                    }
                    HallItem oldTable = reservation.getTable();
                    HallItem table = hallItemRepository
                            .findById(tableId)
                            .orElseThrow(notFoundExceptionThrow(HallItem.class.getSimpleName(), "id", tableId));
                    reservation.setTableId(tableId);
                    if (ReservationStatus.ACTIVE.equals(reservation.getStatus())) {
                        table.setStatus(HallItemStatus.ACTIVE);
                    } else if (ReservationStatus.NEW.equals(reservation.getStatus())) {
                        table.setStatus(HallItemStatus.BOOKED);
                    }
                    oldTable.setStatus(HallItemStatus.FREE);
                    hallItemRepository.saveAll(List.of(table, oldTable));
                    reservationRepository.save(reservation);
                    return new CheckStatusResponseDTO(true, 0);
                })
                .orElseThrow(notFoundExceptionThrow(Reservation.class.getSimpleName(), "id", id));
    }

    @Transactional
    public void activateReservation() {
        reservationRepository.findAllByStatusAndDeletedIsFalseAndFromDateIsAfter(ReservationStatus.NEW, LocalDateTime.now().with(LocalTime.MIN))
                .forEach(reservation -> {
                    if (reservation.getFromDate().minusHours(1).isBefore(LocalDateTime.now())) {
                        log.info("\nReservation Number: {}. date: {}", reservation.getReservationNumber(), reservation.getFromDate());
                        HallItem table = reservation.getTable();
                        if (HallItemStatus.FREE.equals(table.getStatus())) {
                            log.info("\nTableId: {}", table.getId());
                            hallItemService.updateStatus(table.getId(), new UpdateStatusDTO(HallItemStatus.BOOKED.getCode(), table.isWithOrder(), reservation.getNumberOfGuest()));
                        }
                        reservation.setStatus(ReservationStatus.ACTIVE);
                        reservationRepository.save(reservation);
                        if (reservation.getOrderId() != null) {
                            Order order = reservation.getOrder();
                            order.setStatus(OrderStatus.PENDING);
                            orderRepository.save(order);
                            order.getItems().forEach(item -> {
                                item.setStatus(OrderItemStatus.PENDING);
                                orderItemRepository.save(item);
                            });
                        }
                    }
                });
    }

    @Transactional
    public void removeReservations() {
        reservationRepository.findAllByStatusInAndDeletedIsFalseAndFromDateIsBefore(Stream.of(ReservationStatus.NEW, ReservationStatus.ACTIVE).collect(Collectors.toList()), LocalDateTime.now())
                .forEach(reservation -> {
                    try {
                        EnumSet<OrderStatus> orderStatuses = EnumSet.of(OrderStatus.ACTIVE, OrderStatus.READY, OrderStatus.PENDING);
                        HallItem table = reservation.getTable();
                        if (table.getOrders().stream().map(order -> orderStatuses.contains(order.getStatus())).findAny().isEmpty()) {
                            hallItemService.updateStatus(reservation.getTableId(), new UpdateStatusDTO(HallItemStatus.FREE.getCode()));
                        }
                        reservation.setStatus(ReservationStatus.CANCELLED);
                        reservationRepository.save(reservation);
                        if (reservation.getOrderId() != null) {
                            Order order = reservation.getOrder();
                            order.setStatus(OrderStatus.CANCELLED);
                            orderRepository.save(order);
                            order.getItems().forEach(item -> {
                                item.setStatus(OrderItemStatus.CANCELLED);
                                orderItemRepository.save(item);
                            });
                        }
                    } catch (Exception e) {
                        log.info("Cron job update reservation error: {}", e.getMessage());
                    }
                });
    }
}