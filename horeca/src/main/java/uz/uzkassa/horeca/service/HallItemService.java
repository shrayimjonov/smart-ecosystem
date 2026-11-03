package uz.uzkassa.horeca.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.horeca.domain.auth.User;
import uz.uzkassa.horeca.domain.hall.Hall;
import uz.uzkassa.horeca.domain.hall.HallItem;
import uz.uzkassa.horeca.domain.order.Reservation;
import uz.uzkassa.horeca.dto.base.CheckStatusResponseDTO;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.dto.base.UpdateStatusDTO;
import uz.uzkassa.horeca.dto.hallitem.*;
import uz.uzkassa.horeca.dto.hallitem.mobile.MobileHallItemDetailDTO;
import uz.uzkassa.horeca.dto.hallitem.mobile.MobileHallTableListDTO;
import uz.uzkassa.horeca.dto.order.mobile.OrderDetailMobileDTO;
import uz.uzkassa.horeca.dto.reservation.CustomerDTO;
import uz.uzkassa.horeca.dto.reservation.CustomerDetailDTO;
import uz.uzkassa.horeca.dto.reservation.ReservationDetailTableDTO;
import uz.uzkassa.horeca.dto.reservation.mobile.MobileReservationDetailDTO;
import uz.uzkassa.horeca.enums.*;
import uz.uzkassa.horeca.filters.HallItemFilter;
import uz.uzkassa.horeca.repository.HallItemRepository;
import uz.uzkassa.horeca.repository.HallRepository;
import uz.uzkassa.horeca.repository.OrderRepository;
import uz.uzkassa.horeca.repository.ReservationRepository;
import uz.uzkassa.horeca.utils.HeaderUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
@RequiredArgsConstructor
public class HallItemService extends BaseService {

    HallItemRepository hallItemRepository;
    HallRepository hallRepository;
    ReservationRepository reservationRepository;
    OrderRepository orderRepository;
    CustomerService customerService;

    protected void validate(HallItemDTO hallItemDTO) {
        if (StringUtils.isEmpty(hallItemDTO.getName())) {
            throw badRequestExceptionThrow("Name is required").get();
        }
        if (hallItemDTO.getCapacity() == null) {
            throw badRequestExceptionThrow("Capacity is required").get();
        }
        if (StringUtils.isNotEmpty(hallItemDTO.getType()) && HallItemType.getByCode(hallItemDTO.getType()) == null) {
            throw badRequestExceptionThrow("Type is invalid").get();
        }
        if (StringUtils.isNotEmpty(hallItemDTO.getShape()) && ShapeType.getByCode(hallItemDTO.getShape()) == null) {
            throw badRequestExceptionThrow("Shape is invalid").get();
        }
        if (hallItemDTO.getHallId() != null) {
            if (hallItemDTO.getId() == null && hallItemRepository.existsByHallIdAndNameAndDeletedIsFalse(hallItemDTO.getHallId(), hallItemDTO.getName())) {
                throw badRequestExceptionThrow("Hall item with this name already exists").get();
            }
            if (hallItemDTO.getId() != null && hallItemRepository.existsByHallIdAndNameAndDeletedIsFalseAndIdNot(hallItemDTO.getHallId(), hallItemDTO.getName(), hallItemDTO.getId())) {
                throw badRequestExceptionThrow("Hall item with this name already exists").get();
            }
        }
    }

    @Transactional
    public HallItemDetailDTO create(Long hallId, HallItemDTO hallItemDTO) {
        hallItemDTO.setHallId(hallId);
        this.validate(hallItemDTO);
        return hallRepository.findById(hallId).map(hall -> {
            HallItem hallItem = new HallItem();
            hallItem.setName(hallItemDTO.getName());
            hallItem.setCode(hallItemDTO.getCode());
            hallItem.setCapacity(hallItemDTO.getCapacity());
            hallItem.setHallId(hallId);
            hallItem.setHall(hall);
            hallItem.setX(hallItemDTO.getX());
            hallItem.setDescription(hallItemDTO.getDescription());
            hallItem.setY(hallItemDTO.getY());
            hallItem.setHeight(hallItemDTO.getHeight());
            hallItem.setWidth(hallItemDTO.getWidth());
            hallItem.setRotationAngle(hallItemDTO.getRotationAngle());
            hallItem.setStatus(HallItemStatus.FREE);
            hallItem.setItemType(HallItemType.getByCode(hallItemDTO.getType()));
            hallItem.setShape(ShapeType.getByCode(hallItemDTO.getShape()));
            hallItemRepository.save(hallItem);

            return get(hallItem.getId());
        }).orElseThrow(notFoundExceptionThrow(Hall.class.getSimpleName(), "id", hallId));
    }

    public HallItemDetailDTO get(Long id) {
        return hallItemRepository.findById(id).map(hallItem -> {
            HallItemDetailDTO hallItemDetailDTO = new HallItemDetailDTO();
            hallItemDetailDTO.setId(hallItem.getId());
            hallItemDetailDTO.setName(hallItem.getName());
            hallItemDetailDTO.setCode(hallItem.getCode());
            hallItemDetailDTO.setCapacity(hallItem.getCapacity());
            if (hallItem.getHall() != null) {
                hallItemDetailDTO.setHall(hallItem.getHall().toCommonDTO());
            } else {
                hallRepository.findById(hallItem.getHallId()).map(hall -> {
                    hallItemDetailDTO.setHall(hall.toCommonDTO());
                    return hall;
                }).orElseThrow(notFoundExceptionThrow(Hall.class.getSimpleName(), "id", hallItem.getHallId()));
            }
            hallItemDetailDTO.setX(hallItem.getX());
            hallItemDetailDTO.setY(hallItem.getY());
            hallItemDetailDTO.setHeight(hallItem.getHeight());
            hallItemDetailDTO.setWidth(hallItem.getWidth());
            hallItemDetailDTO.setRotationAngle(hallItem.getRotationAngle());
            hallItemDetailDTO.setType(hallItem.getItemType());
            hallItemDetailDTO.setShape(hallItem.getShape());
            hallItemDetailDTO.setLastModifiedDate(hallItem.getUpdatedDate());
            if (hallItem.getMergedTableId() != null) {
                hallItemDetailDTO.setMergedTable(hallItem.getMergedTable().toCommonDTO());
            }
            return hallItemDetailDTO;
        }).orElseThrow(notFoundExceptionThrow(HallItem.class.getSimpleName(), "id", id));
    }

    @Transactional
    public void batchCreate(Long hallId, HallItemBatchDTO hallItemBatchDTO) {
        if (!CollectionUtils.isEmpty(hallItemBatchDTO.getItems())) {
            hallItemBatchDTO.getItems().forEach(hallItemDTO -> create(hallId, hallItemDTO));
        }
    }

    @Transactional
    public void batchUpdate(Long hallId, HallItemBatchDTO hallItemBatchDTO) {
        if (!CollectionUtils.isEmpty(hallItemBatchDTO.getItems())) {
            hallRepository.findById(hallId).ifPresentOrElse(hall -> {
                Map<Long, HallItem> hallItemMap = hall.getItems().stream().collect(Collectors.toMap(HallItem::getId, hallItem -> hallItem, (a, b) -> b));

                hallItemBatchDTO.getItems().forEach(hallItemDTO -> {
                    update(hallItemDTO.getId(), hallItemDTO);
                    hallItemMap.remove(hallItemDTO.getId());
                });
                if (!hallItemMap.isEmpty()) {
                    hallItemRepository.deleteAll(hallItemMap.values());
                }
            }, () -> notFoundExceptionThrow(Hall.class.getSimpleName(), "id", hallId).get());
        }
    }

    public MobileHallItemDetailDTO getForMobile(Long id) {
        return hallItemRepository.findById(id).map(hallItem -> {
            MobileHallItemDetailDTO hallItemDetailDTO = new MobileHallItemDetailDTO();
            hallItemDetailDTO.setId(hallItem.getId());
            hallItemDetailDTO.setName(hallItem.getName());
            hallItemDetailDTO.setCode(hallItem.getCode());
            hallItemDetailDTO.setCapacity(hallItem.getCapacity());
            hallItemDetailDTO.setNumberOfGuest(hallItem.getNumberOfGuest());
            if (hallItem.getHall() != null) {
                hallItemDetailDTO.setHall(hallItem.getHall().toCommonDTO());
            } else {
                hallRepository.findById(hallItem.getHallId()).map(hall -> {
                    hallItemDetailDTO.setHall(hall.toCommonDTO());
                    return hall;
                }).orElseThrow(notFoundExceptionThrow(Hall.class.getSimpleName(), "id", hallItem.getHallId()));
            }
            hallItemDetailDTO.setStatus(hallItem.getStatus());
            if (hallItem.getMergedTableId() != null) {
                hallItemDetailDTO.setMergedTable(hallItem.getMergedTable().toCommonDTO());
            }
            hallItemDetailDTO.setMergedTables(hallItem.getMergedTables().stream().map(HallItem::toCommonCodeDTO).collect(Collectors.toList()));
            reservationRepository.findAllByTableIdAndDeletedIsFalseAndStatusInOrderByFromDate(hallItem.getId(), new ArrayList<>(EnumSet.of(ReservationStatus.NEW, ReservationStatus.ACTIVE))).forEach(reservation -> {
                MobileReservationDetailDTO mobileReservationDetailDTO = new MobileReservationDetailDTO();
                mobileReservationDetailDTO.setId(reservation.getId());
                mobileReservationDetailDTO.setReservationNumber(reservation.getReservationNumber());
                mobileReservationDetailDTO.setFromDate(reservation.getFromDate());
                mobileReservationDetailDTO.setToDate(reservation.getToDate());
                mobileReservationDetailDTO.setNumberOfGuest(reservation.getNumberOfGuest());
                mobileReservationDetailDTO.setClient(reservation.getClient().toCommonDTO());
                mobileReservationDetailDTO.setGuestName(reservation.getGuestName());
                if (reservation.getOrder() != null) {
                    mobileReservationDetailDTO.setOrder(reservation.getOrder().toCommonDTO());
                }
                mobileReservationDetailDTO.setComment(reservation.getComment());
                mobileReservationDetailDTO.setStatus(reservation.getStatus());
                hallItemDetailDTO.addReservation(mobileReservationDetailDTO);
            });
            orderRepository.findAllByTableIdAndDeletedIsFalseAndStatusNotIn(hallItem.getId(), new ArrayList<>(EnumSet.of(OrderStatus.CANCELLED, OrderStatus.BOOKED, OrderStatus.CLOSED))).forEach(order -> {
                OrderDetailMobileDTO mobileOrderDetailDTO = new OrderDetailMobileDTO();
                mobileOrderDetailDTO.setId(order.getId());
                mobileOrderDetailDTO.setOrderNumber(order.getOrderNumber());
                mobileOrderDetailDTO.setStatus(order.getStatus());
                mobileOrderDetailDTO.setReason(order.getReason());
                mobileOrderDetailDTO.setOrderDate(order.getOrderDate());
                mobileOrderDetailDTO.setTable(order.getTable().toCommonCodeDTO());
                mobileOrderDetailDTO.setPaymentStatus(order.getPaymentStatus());
                mobileOrderDetailDTO.setNumberOfGuest(order.getNumberOfGuest());
                mobileOrderDetailDTO.setTotalPrice(order.getTotalPrice());
                mobileOrderDetailDTO.setTotalQty(order.getTotalQty());
                hallItemDetailDTO.addOrder(mobileOrderDetailDTO);
            });
            return hallItemDetailDTO;
        }).orElseThrow(notFoundExceptionThrow(HallItem.class.getSimpleName(), "id", id));
    }

    @Transactional
    public Long update(Long id, HallItemDTO hallItemDTO) {
        return hallItemRepository.findById(id).map(hallItem -> {
            hallItemDTO.setId(hallItem.getHallId());
            hallItemDTO.setHallId(id);
            this.validate(hallItemDTO);

            hallItem.setName(hallItemDTO.getName());
            hallItem.setCode(hallItemDTO.getCode());
            hallItem.setCapacity(hallItemDTO.getCapacity());
            hallItem.setX(hallItemDTO.getX());
            hallItem.setY(hallItemDTO.getY());
            hallItem.setHeight(hallItemDTO.getHeight());
            hallItem.setWidth(hallItemDTO.getWidth());
            hallItem.setDescription(hallItemDTO.getDescription());
            hallItem.setRotationAngle(hallItemDTO.getRotationAngle());
            hallItem.setItemType(HallItemType.getByCode(hallItemDTO.getType()));
            hallItem.setShape(ShapeType.getByCode(hallItemDTO.getShape()));
            hallItemRepository.save(hallItem);
            return hallItem.getId();
        }).orElseThrow(notFoundExceptionThrow(HallItem.class.getSimpleName(), "id", id));
    }

    @Transactional
    public void delete(Long id) {
        hallItemRepository.findById(id).map(hallItem -> {
            if (orderRepository.existsByTableIdAndDeletedIsFalse(id)) {
                throw badRequestExceptionThrow(localizationService.localize("hall.item.delete.not.allowed")).get();
            }
            if (reservationRepository.existsByTableIdAndDeletedIsFalse(id)) {
                throw badRequestExceptionThrow(localizationService.localize("hall.item.delete.not.allowed")).get();
            }
            hallItem.setDeleted(true);
            hallItemRepository.save(hallItem);
            return hallItem;
        }).orElseThrow(notFoundExceptionThrow(HallItem.class.getSimpleName(), "id", id));
    }

    @Transactional
    public void updateStatus(Long id, UpdateStatusDTO updateStatusDTO) {
        if (updateStatusDTO.getStatus() == null) {
            throw badRequestExceptionThrow("Status is required").get();
        }
        if (HallItemStatus.getByCode(updateStatusDTO.getStatus()) == null) {
            throw badRequestExceptionThrow("Status is invalid").get();
        }
        if (StringUtils.isNotEmpty(updateStatusDTO.getReason()) && HallItemReason.getByCode(updateStatusDTO.getReason()) == null) {
            throw badRequestExceptionThrow("Reason is invalid").get();
        }
        hallItemRepository.findById(id).map(hallItem -> {
            if (HallItemStatus.IN_ACTIVE.equals(HallItemStatus.valueOf(updateStatusDTO.getStatus()))) {
                if (orderRepository.existsByTableIdAndDeletedIsFalseAndStatusNotIn(id, List.of(OrderStatus.CLOSED, OrderStatus.CANCELLED))) {
                    throw badRequestExceptionThrow(localizationService.localize("hall.item.delete.not.allowed")).get();
                }
                if (reservationRepository.existsByTableIdAndDeletedIsFalseAndStatusIn(id, List.of(ReservationStatus.ACTIVE, ReservationStatus.NEW))) {
                    throw badRequestExceptionThrow(localizationService.localize("hall.item.delete.not.allowed")).get();
                }
            }
            if (HallItemStatus.ACTIVE.equals(hallItem.getStatus()) && HallItemStatus.BOOKED.getCode().equals(updateStatusDTO.getStatus())) {
                return hallItem;
            }
            hallItem.setStatus(HallItemStatus.valueOf(updateStatusDTO.getStatus()));
            if (StringUtils.isNotEmpty(updateStatusDTO.getReason())) {
                hallItem.setReason(HallItemReason.valueOf(updateStatusDTO.getReason()));
            }
            hallItem.setOtherReason(updateStatusDTO.getOtherReason());
            if (HallItemStatus.FREE.getCode().equals(updateStatusDTO.getStatus())) {
                hallItem.setWithOrder(false);
                hallItem.setNumberOfGuest(null);
                hallItem.setClient(null);
                hallItem.setClientId(null);
                if (!CollectionUtils.isEmpty(hallItem.getMergedTables())) {
                    hallItem.getMergedTables().forEach(mergedHallItem -> {
                        mergedHallItem.setStatus(HallItemStatus.FREE);
                        mergedHallItem.setMergedTableId(null);
                    });
                    hallItemRepository.saveAll(hallItem.getMergedTables());
                }
            }

            if (HallItemStatus.ACTIVE.equals(HallItemStatus.valueOf(updateStatusDTO.getStatus()))) {
                hallItem.setWithOrder(updateStatusDTO.isWithOrder());
                if (StringUtils.isNotEmpty(updateStatusDTO.getGuestPhone()) && StringUtils.isNotEmpty(updateStatusDTO.getGuestName())) {
                    User client = customerService.createEntity(new CustomerDTO(updateStatusDTO.getGuestName(), updateStatusDTO.getGuestPhone(), Optional.ofNullable(hallItem.getHall().getBranchId()).orElse(getCurrentBranchId())));
                    hallItem.setClientId(client.getId());
                }
                hallItem.setNumberOfGuest(updateStatusDTO.getNumberOfGuest());
            }
            if (hallItem.getMergedTableId() != null) {
                hallItem.setMergedTableId(null);
            }
            return hallItemRepository.save(hallItem);
        }).orElseThrow(notFoundExceptionThrow(HallItem.class.getSimpleName(), "id", id));
    }

    @Transactional
    public void merge(HallItemMergeDTO hallItemMergeDTO) {
        if (hallItemMergeDTO.getActiveId() == null) {
            throw badRequestExceptionThrow("Active id is required").get();
        }
        if (CollectionUtils.isEmpty(hallItemMergeDTO.getBlockingIds())) {
            throw badRequestExceptionThrow("Blocking ids are required").get();
        }
        if (hallItemMergeDTO.getStatus() == null) {
            throw badRequestExceptionThrow("Status is required").get();
        }
        if (HallItemStatus.getByCode(hallItemMergeDTO.getStatus()) == null) {
            throw badRequestExceptionThrow("Status is invalid").get();
        }
        if (StringUtils.isNotEmpty(hallItemMergeDTO.getReason()) && HallItemReason.getByCode(hallItemMergeDTO.getReason()) == null) {
            throw badRequestExceptionThrow("Reason is invalid").get();
        }

        hallItemRepository.findById(hallItemMergeDTO.getActiveId()).ifPresentOrElse(hallItem -> {
            List<HallItem> result = hallItemRepository.findAllById(hallItemMergeDTO.getBlockingIds()).stream().peek(blockingHallItem -> {
                blockingHallItem.setStatus(HallItemStatus.valueOf(hallItemMergeDTO.getStatus()));
                if (StringUtils.isNotEmpty(hallItemMergeDTO.getReason())) {
                    blockingHallItem.setReason(HallItemReason.valueOf(hallItemMergeDTO.getReason()));
                }
                blockingHallItem.setOtherReason(hallItemMergeDTO.getOtherReason());
                blockingHallItem.setMergedTableId(hallItem.getId());
            }).collect(Collectors.toList());
            hallItem.setStatus(HallItemStatus.ACTIVE);
            result.add(hallItem);
            hallItemRepository.saveAll(result);
        }, () -> notFoundExceptionThrow(HallItem.class.getSimpleName(), "id", hallItemMergeDTO.getActiveId()));
    }

    public List<MobileHallTableListDTO> getTablesForMobile(Long hallId) {
        return hallItemRepository.findAllByHallIdAndDeletedIsFalseOrderByIdAsc(hallId).stream().map(hallItem -> {
            MobileHallTableListDTO hallTableListDTO = new MobileHallTableListDTO();
            hallTableListDTO.setId(hallItem.getId());
            hallTableListDTO.setName(hallItem.getName());
            hallTableListDTO.setCapacity(hallItem.getCapacity());
            hallTableListDTO.setStatus(hallItem.getStatus());
            hallTableListDTO.setWithOrder(hallItem.isWithOrder());
            hallTableListDTO.setNumberOfGuest(hallItem.getNumberOfGuest());
            hallTableListDTO.setMessage(getHallItemMessage(HeaderUtils.getCurrentRequestLanguageEnum()));
            return hallTableListDTO;
        }).collect(Collectors.toList());
    }

    public CheckStatusResponseDTO check(Long id, HallItemCheckDTO hallItemCheckDTO) {
        if (hallItemCheckDTO.getReservationStartDate() == null) {
            badRequestExceptionThrow("Start date is required").get();
        }
        if (hallItemCheckDTO.getReservationEndDate() == null) {
            hallItemCheckDTO.setReservationEndDate(hallItemCheckDTO.getReservationStartDate().plusHours(1));
        }
        return hallItemRepository.findById(id).map(hallItem -> {
            if (HallItemStatus.BLOCKED.equals(hallItem.getStatus())) {
                return new CheckStatusResponseDTO(1);
            }
                    /*if (hallItemCheckDTO.getNumberOfGuests() != null && hallItem.getCapacity() < hallItemCheckDTO.getNumberOfGuests()) {
                        Long capacity = hallItem.getCapacity();
                        if (!CollectionUtils.isEmpty(hallItem.getMergedTables())) {
                            for (HallItem mergedTable : hallItem.getMergedTables()) {
                                capacity += mergedTable.getCapacity();
                            }
                        }
                        if (capacity < hallItemCheckDTO.getNumberOfGuests()) {
                            return new CheckStatusResponseDTO(2);
                        }
                    }*/
            if (LocalDateTime.now().isAfter(hallItemCheckDTO.getReservationStartDate())) {
                return new CheckStatusResponseDTO(1);
            }
            for (Reservation reservation : hallItem.getReservations()) {
                if (hallItemCheckDTO.getReservationStartDate().isBefore(reservation.getFromDate()) || hallItemCheckDTO.getReservationStartDate().isEqual(reservation.getFromDate())) {
                    if (hallItemCheckDTO.getReservationEndDate().isAfter(reservation.getFromDate().minusHours(1))) {
                        return new CheckStatusResponseDTO(1);
                    }
                } else if (hallItemCheckDTO.getReservationStartDate().isAfter(reservation.getFromDate())) {
                    if (Optional.ofNullable(reservation.getToDate()).orElse(reservation.getFromDate().plusHours(1)).isAfter(hallItemCheckDTO.getReservationStartDate())) {
                        return new CheckStatusResponseDTO(1);
                    }
                }
            }
            if (HallItemStatus.ACTIVE.equals(hallItem.getStatus())) {
                if (LocalDateTime.now().plusHours(1).isAfter(hallItemCheckDTO.getReservationStartDate())) {
                    return new CheckStatusResponseDTO(1);
                }
            }

            return new CheckStatusResponseDTO(true, 0);
        }).orElseThrow(notFoundExceptionThrow(HallItem.class.getSimpleName(), "id", id));
    }

    public List<HallTableListDTO> getTables(Long hallId) {
        return hallItemRepository.findAllByHallIdAndDeletedIsFalseOrderByIdAsc(hallId).stream().map(hallItem -> {
            HallTableListDTO hallTableListDTO = new HallTableListDTO();
            hallTableListDTO.setId(hallItem.getId());
            hallTableListDTO.setName(hallItem.getName());
            hallTableListDTO.setCapacity(hallItem.getCapacity());
            hallTableListDTO.setStatus(hallItem.getStatus());
            hallTableListDTO.setWithOrder(hallItem.isWithOrder());
            hallTableListDTO.setMessage(getHallItemMessage(HeaderUtils.getCurrentRequestLanguageEnum()));
            hallTableListDTO.setNumberOfGuest(hallItem.getNumberOfGuest());
            hallTableListDTO.setFeePercent(hallItem.getFeePercent());
            return hallTableListDTO;
        }).collect(Collectors.toList());
    }

    public List<HallTableListDTO> getTablesForTerminal(Long hallId) {
        return hallItemRepository.findAllByHallIdAndDeletedIsFalseAndStatusOrderByIdAsc(hallId, HallItemStatus.ACTIVE).stream().map(hallItem -> {
            HallTableListDTO hallTableListDTO = new HallTableListDTO();
            hallTableListDTO.setId(hallItem.getId());
            hallTableListDTO.setName(hallItem.getName());
            hallTableListDTO.setCapacity(hallItem.getCapacity());
            hallTableListDTO.setStatus(hallItem.getStatus());
            hallTableListDTO.setWithOrder(hallItem.isWithOrder());
            hallTableListDTO.setNumberOfGuest(hallItem.getNumberOfGuest());
            hallTableListDTO.setMessage(getHallItemMessage(HeaderUtils.getCurrentRequestLanguageEnum()));
            return hallTableListDTO;
        }).collect(Collectors.toList());
    }

    public List<HallItemListDTO> getItems(Long hallId) {
        HallItemFilter filter = new HallItemFilter();
        filter.setHallId(hallId);
        filter.setSize(10000);
        return getList(filter).getContent();
    }

    public Page<HallItemListDTO> getList(HallItemFilter filter) {
        ResultList<HallItem> resultList = hallItemRepository.getResultList(filter);
        List<HallItemListDTO> result = resultList.getList().stream().map(hallItem -> {
            HallItemListDTO hallItemListDTO = new HallItemListDTO();
            hallItemListDTO.setId(hallItem.getId());
            hallItemListDTO.setName(hallItem.getName());
            hallItemListDTO.setCode(hallItem.getCode());
            hallItemListDTO.setCapacity(hallItem.getCapacity());
            hallItemListDTO.setX(hallItem.getX());
            hallItemListDTO.setY(hallItem.getY());
            hallItemListDTO.setHeight(hallItem.getHeight());
            hallItemListDTO.setWidth(hallItem.getWidth());
            hallItemListDTO.setRotationAngle(hallItem.getRotationAngle());
            hallItemListDTO.setType(hallItem.getItemType());
            hallItemListDTO.setStatus(hallItem.getStatus());
            hallItemListDTO.setDescription(hallItem.getDescription());
            hallItemListDTO.setShape(hallItem.getShape());
            hallItemListDTO.setHall(hallItem.getHall().toCommonDTO());
            hallItemListDTO.setWithOrder(hallItem.isWithOrder());
            hallItemListDTO.setMessage(getHallItemMessage(HeaderUtils.getCurrentRequestLanguageEnum()));
            hallItemListDTO.setFeePercent(hallItem.getFeePercent());
            return hallItemListDTO;
        }).collect(Collectors.toList());
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    public HallItemTableDetailDTO getTable(Long id) {
        return hallItemRepository.findById(id).map(hallItem -> {
            HallItemTableDetailDTO hallItemDetailDTO = new HallItemTableDetailDTO();
            hallItemDetailDTO.setId(hallItem.getId());
            hallItemDetailDTO.setName(hallItem.getName());
            hallItemDetailDTO.setCapacity(hallItem.getCapacity());
            if (hallItem.getHall() != null) {
                hallItemDetailDTO.setHall(hallItem.getHall().toCommonDTO());
            } else {
                hallRepository.findById(hallItem.getHallId()).map(hall -> {
                    hallItemDetailDTO.setHall(hall.toCommonDTO());
                    return hall;
                }).orElseThrow(notFoundExceptionThrow(Hall.class.getSimpleName(), "id", hallItem.getHallId()));
            }
            hallItemDetailDTO.setStatus(hallItem.getStatus());
            hallItemDetailDTO.setWithOrder(hallItem.isWithOrder());
            hallItemDetailDTO.setNumberOfGuest(hallItem.getNumberOfGuest());
            hallItemDetailDTO.setLastModifiedDate(hallItem.getUpdatedDate());
            hallItemDetailDTO.setFeePercent(hallItem.getFeePercent());
            if (hallItem.getMergedTableId() != null) {
                hallItemDetailDTO.setMergedTable(hallItem.getMergedTable().toCommonDTO());
            }
            reservationRepository.findAllByTableIdAndDeletedIsFalseAndStatusInOrderByFromDate(hallItem.getId(), new ArrayList<>(EnumSet.of(ReservationStatus.NEW, ReservationStatus.ACTIVE))).forEach(reservation -> {
                ReservationDetailTableDTO reservationDetailTableDTO = new ReservationDetailTableDTO();
                reservationDetailTableDTO.setId(reservation.getId());
                reservationDetailTableDTO.setReservationNumber(reservation.getReservationNumber());
                reservationDetailTableDTO.setNumberOfGuest(reservation.getNumberOfGuest());
                reservationDetailTableDTO.setFromDate(reservation.getFromDate());
                reservationDetailTableDTO.setToDate(reservation.getToDate());
                reservationDetailTableDTO.setStatus(reservation.getStatus());
                reservationDetailTableDTO.setLastModifiedDate(reservation.getUpdatedDate());
                reservationDetailTableDTO.setGuessName(reservation.getGuestName());
                if (reservation.getClient() != null) {
                    CustomerDetailDTO reservationClientDTO = new CustomerDetailDTO();
                    reservationClientDTO.setId(reservation.getClient().getId());
                    reservationClientDTO.setName(reservation.getClient().getName());
                    reservationClientDTO.setPhone(reservation.getClient().getPhone());
                    reservationDetailTableDTO.setClient(reservationClientDTO);
                }
                if (reservation.getOrder() != null) {
                    reservationDetailTableDTO.setOrder(reservation.getOrder().toCommonDTO());

                }
                reservationDetailTableDTO.setCreator(reservation.getCreator().toCommonDTO());
                hallItemDetailDTO.addReservation(reservationDetailTableDTO);
            });
            orderRepository.findAllByTableIdAndDeletedIsFalseAndStatusNotIn(hallItem.getId(), new ArrayList<>(EnumSet.of(OrderStatus.CANCELLED, OrderStatus.BOOKED, OrderStatus.CLOSED)))
                    .forEach(order -> hallItemDetailDTO.addOrder(order.toCommonDTO()));
            return hallItemDetailDTO;
        }).orElseThrow(notFoundExceptionThrow(HallItem.class.getSimpleName(), "id", id));
    }

    public HallItemTableStatisticsDTO getStatistics(HallItemFilter filter) {
        return hallItemRepository.getTableStatistics(filter).getFirst();
    }
}
