package uz.uzkassa.horeca.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.horeca.domain.auth.User;
import uz.uzkassa.horeca.domain.hall.HallItem;
import uz.uzkassa.horeca.domain.menu.MenuItem;
import uz.uzkassa.horeca.domain.order.Order;
import uz.uzkassa.horeca.domain.order.OrderItem;
import uz.uzkassa.horeca.domain.order.Reservation;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.dto.base.UpdateStatusDTO;
import uz.uzkassa.horeca.dto.order.OrderDTO;
import uz.uzkassa.horeca.dto.order.OrderItemBaseDTO;
import uz.uzkassa.horeca.dto.order.admin.OrderDetailAdminDTO;
import uz.uzkassa.horeca.dto.order.admin.OrderItemDetailAdminDTO;
import uz.uzkassa.horeca.dto.order.admin.OrderListAdminDTO;
import uz.uzkassa.horeca.dto.order.cabinet.OrderDetailCabinetDTO;
import uz.uzkassa.horeca.dto.order.cabinet.OrderItemDetailCabinetDTO;
import uz.uzkassa.horeca.dto.order.cabinet.OrderListCabinetDTO;
import uz.uzkassa.horeca.dto.order.cabinet.OrderShortListCabinetDTO;
import uz.uzkassa.horeca.dto.order.kanban.OrderKanbanBoardDetailDTO;
import uz.uzkassa.horeca.dto.order.kanban.OrderKanbanBoardListDTO;
import uz.uzkassa.horeca.dto.order.kanban.OrderStatisticsDTO;
import uz.uzkassa.horeca.dto.order.mobile.*;
import uz.uzkassa.horeca.dto.payload.OrderEventPayload;
import uz.uzkassa.horeca.dto.reservation.CustomerDTO;
import uz.uzkassa.horeca.dto.reservation.CustomerDetailDTO;
import uz.uzkassa.horeca.dto.terminal.BillListDTO;
import uz.uzkassa.horeca.enums.*;
import uz.uzkassa.horeca.executor.AfterCommitExecutor;
import uz.uzkassa.horeca.filters.OrderFilter;
import uz.uzkassa.horeca.rabbitmq.producer.RabbitMqOrderProducer;
import uz.uzkassa.horeca.repository.*;
import uz.uzkassa.horeca.utils.CalcUtils;
import uz.uzkassa.horeca.utils.DateUtils;
import uz.uzkassa.horeca.utils.HeaderUtils;
import uz.uzkassa.horeca.utils.MarkUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
@RequiredArgsConstructor
@Primary
@Slf4j
public class OrderService extends BaseService {

    OrderRepository orderRepository;
    OrderItemRepository orderItemRepository;
    HallItemService hallItemService;
    HallItemRepository hallItemRepository;
    ReservationRepository reservationRepository;
    MenuItemRepository menuItemRepository;
    CustomerService customerService;
    ShiftRepository shiftRepository;
    RabbitMqOrderProducer rabbitMqOrderProducer;
    AfterCommitExecutor afterCommitExecutor;

    public HashMap<String, String> getNumberingMap(Long branchId) {
        Long incrementNumber = orderRepository.getMaxIncrementNumber(branchId) + 1L;
        String orderNumber = Prefix.ORDER
                .getName()
                .concat(DateUtils.formatYearLastTwoDigit(LocalDate.now()))
                .concat("-")
                .concat(COMMON_NUMBERING_FORMAT.format(incrementNumber));

        HashMap<String, String> numberMap = new HashMap<>();
        numberMap.put(INCREMENT_NUMBER, String.valueOf(incrementNumber));
        numberMap.put(NUMBER, orderNumber);
        return numberMap;
    }

    public void validate(OrderDTO orderDTO) {
        getCurrentBranchId(orderDTO.getBranchId());
        if (orderDTO.getTableId() == null) {
            throw badRequestExceptionThrow("Table is required").get();
        }
        if (orderDTO.getTotalPrice() == null) {
            throw badRequestExceptionThrow("Total price is required").get();
        }
        if (orderDTO.getTotalQty() == null) {
            throw badRequestExceptionThrow("Total qty is required").get();
        }
//        if (!shiftRepository.existsByBranchIdAndStatus(orderDTO.getBranchId(),ShiftStatus.OPEN)){
//            throw badRequestExceptionThrow("Open shift to create order").get();
//        }
    }

    private void validateItem(OrderItemBaseDTO orderItemDTO) {
        if (orderItemDTO.getMenuItemId() == null) {
            throw badRequestExceptionThrow("Menu item id is required").get();
        }
        if (orderItemDTO.getMultiUnitId() == null) {
            throw badRequestExceptionThrow("Multi unit id is required").get();
        }
        if (orderItemDTO.getPrice() == null || orderItemDTO.getPrice().compareTo(BigDecimal.ZERO) == 0) {
            throw badRequestExceptionThrow("Price is required").get();
        }
        if (orderItemDTO.getQty() == null || orderItemDTO.getQty().compareTo(BigDecimal.ZERO) == 0) {
            throw badRequestExceptionThrow("Qty is required").get();
        }
        if (orderItemDTO.getTotalPrice() == null || orderItemDTO.getTotalPrice().compareTo(BigDecimal.ZERO) == 0) {
            throw badRequestExceptionThrow("Total price is required").get();
        }
    }

    @Transactional
    public Long create(OrderDTO orderDTO) {
        Long branchId = getCurrentBranchId(orderDTO.getBranchId());
        orderDTO.setBranchId(branchId);
        this.validate(orderDTO);
        HallItem table = hallItemRepository.findById(orderDTO.getTableId()).orElseThrow(notFoundExceptionThrow(HallItem.class.getSimpleName(), "id", orderDTO.getTableId()));
        HashMap<String, String> numberingMap = this.getNumberingMap(branchId);

        BigDecimal totalSellingPrice = orderDTO.getTotalPrice();
        BigDecimal totalVatPrice = BigDecimal.ZERO;

        Order order = new Order();
        order.setIncrementNumber(Long.valueOf(numberingMap.get(INCREMENT_NUMBER)));
        order.setOrderNumber(numberingMap.get(NUMBER));
        order.setUid(String.valueOf(System.currentTimeMillis()).concat(getCurrentUserId().toString()));
        order.setBranchId(branchId);
        order.setWaiterId(Optional.ofNullable(orderDTO.getWaiterId()).orElseGet(HeaderUtils::getCurrentUserId));
        order.setOrderDate(LocalDateTime.now());
        shiftRepository.findFirstByUserIdAndStatusOrderByCreatedDateDesc(getCurrentUserId(), ShiftStatus.OPEN).ifPresent(shift -> order.setShiftId(shift.getId()));

        order.setTotalPrice(orderDTO.getTotalPrice());
        order.setTotalQty(orderDTO.getTotalQty());
        order.setTotalSellingPrice(totalSellingPrice);
        order.setPaymentStatus(orderDTO.getPaymentStatus());
        order.setPaymentType(orderDTO.getPaymentType());
        order.setComment(orderDTO.getComment());
        order.setNumberOfGuest(orderDTO.getNumberOfGuest());
        order.setTableId(table.getId());
        order.setTable(table);
        order.setItemsCount(orderDTO.getItems().size());
        orderRepository.save(order);
        table.setWithOrder(true);
        hallItemRepository.save(table);

        if (orderDTO.getReservationId() != null) {
            order.setStatus(OrderStatus.BOOKED);
            reservationRepository
                    .findById(orderDTO.getReservationId()).map(reservation -> {
                        order.setReservationId(reservation.getId());
                        order.setClientId(reservation.getClientId());
                        order.setReservation(reservation);
                        reservation.setOrderId(order.getId());
                        reservation.setOrder(order);
                        reservationRepository.save(reservation);
                        return reservation;
                    })
                    .orElseThrow(notFoundExceptionThrow(Reservation.class.getSimpleName(), "id", orderDTO.getReservationId()));
        } else {
            hallItemService.updateStatus(orderDTO.getTableId(), new UpdateStatusDTO(HallItemStatus.ACTIVE.getCode(), orderDTO.getNumberOfGuest()));
        }
        if (table.getClientId() != null) {
            order.setClientId(table.getClientId());
        } else if (StringUtils.isNotEmpty(orderDTO.getGuestPhone()) && StringUtils.isNotEmpty(orderDTO.getGuestName())) {
            User client = customerService.createEntity(new CustomerDTO(orderDTO.getGuestName(), orderDTO.getGuestPhone(), order.getBranchId()));
            order.setClientId(client.getId());
        }
        for (OrderItemBaseDTO orderItemDTO : orderDTO.getItems()) {
            this.validateItem(orderItemDTO);
            MenuItem menuItem = menuItemRepository.findById(orderItemDTO.getMenuItemId()).orElseThrow(notFoundExceptionThrow(MenuItem.class.getSimpleName(), "id", orderItemDTO.getMenuItemId()));
            OrderItem orderItem = new OrderItem();
            orderItem.setComment(orderItemDTO.getComment());
            orderItem.setMenuItemId(menuItem.getId());
            orderItem.setMultiUnitId(orderItemDTO.getMultiUnitId());
            orderItem.setQty(orderItemDTO.getQty());
            orderItem.setPrice(orderItemDTO.getPrice());
            orderItem.setTotalPrice(orderItemDTO.getTotalPrice());
            if (!CollectionUtils.isEmpty(orderItemDTO.getMarks())) {
                if (menuItem.getProduct() != null && StringUtils.isEmpty(menuItem.getProduct().getBarcode())) {
                    throw badRequestExceptionThrow("Product barcode is null").get();
                }
                orderItemDTO.getMarks().forEach(code -> {
                    if (MarkUtils.isNotMark(code)) {
                        throw badRequestExceptionThrow("Incorrect marking code").get();
                    }
                    if (!code.contains(menuItem.getProduct().getBarcode())) {
                        throw badRequestExceptionThrow(localizationService.localize("incorrect.product")).get();
                    }
                    orderItem.addMark(MarkUtils.clearCode(code));
                });
            }
            if (menuItem.getProduct().hasVat()) {
                orderItem.setVatRate(menuItem.getProduct().getVat().getAmount());
                BigDecimal vatPrice = CalcUtils.calcVat(orderItemDTO.getTotalPrice(), menuItem.getProduct().getVat().getAmount());
                orderItem.setVatPrice(vatPrice);
                totalVatPrice = totalVatPrice.add(vatPrice);
            }
            if (OrderStatus.BOOKED.equals(order.getStatus())) {
                orderItem.setStatus(OrderItemStatus.BOOKED);
            }

            order.addItem(orderItem);
        }
        order.setTotalVat(totalVatPrice);

        calculateFeeAmount(order);
        orderRepository.save(order);
        if (!OrderStatus.PENDING.equals(order.getStatus())) {
            afterCommitExecutor.execute(() -> rabbitMqOrderProducer.sendEvent(new OrderEventPayload(order.getId(), OrderEventType.ORDER_CREATE)));
        }
        return order.getId();
    }

    @Transactional
    public Long update(Long id, OrderDTO orderDTO) {
        orderDTO.setId(id);
        this.validate(orderDTO);
        return orderRepository.findById(id).map(order -> {
            order.setClientId(orderDTO.getClientId());
            order.setWaiterId(Optional.ofNullable(orderDTO.getWaiterId()).orElse(HeaderUtils.getCurrentUserId()));
            order.setPaymentStatus(orderDTO.getPaymentStatus());
            order.setPaymentType(orderDTO.getPaymentType());
            order.setNumberOfGuest(orderDTO.getNumberOfGuest());
            order.setComment(orderDTO.getComment());
            order.setTotalPrice(orderDTO.getTotalPrice());
            order.setTotalQty(orderDTO.getTotalQty());
            order.setTotalSellingPrice(orderDTO.getTotalPrice());

            if (orderDTO.getTableId().equals(order.getTableId())) {
                hallItemRepository.findById(order.getTableId()).ifPresentOrElse(table -> {
                    order.setTableId(table.getId());
                    order.setTable(table);
                    hallItemService.updateStatus(orderDTO.getTableId(), new UpdateStatusDTO(HallItemStatus.ACTIVE.getCode(), true, order.getNumberOfGuest()));
                }, () -> notFoundExceptionThrow(HallItem.class.getSimpleName(), "id", order.getTableId()));
            }

            Map<Long, OrderItem> orderItemsMap = order
                    .getItems()
                    .stream()
                    .collect(Collectors.toMap(OrderItem::getId, Function.identity()));
            BigDecimal totalVatPrice = BigDecimal.ZERO;
            for (OrderItemBaseDTO orderItemDTO : orderDTO.getItems()) {
                this.validateItem(orderItemDTO);
                MenuItem menuItem = menuItemRepository.findById(orderItemDTO.getMenuItemId()).orElseThrow(notFoundExceptionThrow(MenuItem.class.getSimpleName(), "id", orderItemDTO.getMenuItemId()));
                final OrderItem orderItem = orderItemsMap.getOrDefault(id, new OrderItem());
                orderItem.setComment(orderItemDTO.getComment());
                orderItem.setMenuItemId(menuItem.getId());
                orderItem.setMultiUnitId(orderItemDTO.getMultiUnitId());
                orderItem.setQty(orderItemDTO.getQty());
                orderItem.setPrice(orderItemDTO.getPrice());
                orderItem.setTotalPrice(orderItemDTO.getTotalPrice());
                if (!CollectionUtils.isEmpty(orderItemDTO.getMarks())) {
                    if (menuItem.getProduct() != null && StringUtils.isEmpty(menuItem.getProduct().getBarcode())) {
                        throw badRequestExceptionThrow("Product barcode is null").get();
                    }
                    orderItemDTO.getMarks().forEach(code -> {
                        if (MarkUtils.isNotMark(code)) {
                            throw badRequestExceptionThrow(localizationService.localize("incorrect.product")).get();
                        }
                        if (!code.contains(menuItem.getProduct().getBarcode())) {
                            throw badRequestExceptionThrow(localizationService.localize("incorrect.product")).get();
                        }
                        orderItem.addMark(MarkUtils.clearCode(code));
                    });
                }
                if (menuItem.getProduct().hasVat()) {
                    orderItem.setVatRate(menuItem.getProduct().getVat().getAmount());
                    BigDecimal vatPrice = CalcUtils.calcVat(orderItemDTO.getTotalPrice(), menuItem.getProduct().getVat().getAmount());
                    orderItem.setVatPrice(vatPrice);
                    totalVatPrice = totalVatPrice.add(vatPrice);
                }
                order.addItem(orderItem);
                if (orderItem.getId() != null) {
                    orderItemsMap.remove(orderItem.getId());
                }
            }
            order.setTotalVat(totalVatPrice);
            order.setItemsCount(orderDTO.getItems().size());
            calculateFeeAmount(order);
            orderRepository.save(order);
            if (!orderItemsMap.isEmpty()) {
                orderItemRepository.deleteAllByIdInBatch(orderItemsMap.keySet());
            }

            afterCommitExecutor.execute(() -> rabbitMqOrderProducer.sendEvent(new OrderEventPayload(order.getId(), OrderEventType.ORDER_UPDATE)));
            return order.getId();
        }).orElseThrow(notFoundExceptionThrow(Order.class.getSimpleName(), "id", id));
    }

    public OrderDetailAdminDTO getForAdmin(Long id) {
        return orderRepository
                .findById(id)
                .map(order -> {
                    OrderDetailAdminDTO orderDetailDTO = new OrderDetailAdminDTO();
                    orderDetailDTO.setId(order.getId());
                    orderDetailDTO.setOrderNumber(order.getOrderNumber());
                    orderDetailDTO.setOrderDate(order.getOrderDate());
                    if (order.getClientId() != null) {
                        orderDetailDTO.setClient(order.getClient().toCommonDTO());
                    }
                    if (order.getWaiterId() != null) {
                        orderDetailDTO.setWaiter(order.getWaiter().toCommonDTO());
                    }
                    orderDetailDTO.setBranch(order.getBranch().toCommonDTO());
                    orderDetailDTO.setStatus(order.getStatus());
                    orderDetailDTO.setTotalPrice(order.getTotalPrice());
                    orderDetailDTO.setTotalQty(order.getTotalQty());
                    orderDetailDTO.setFeePercent(order.getFeePercent());
                    orderDetailDTO.setFeeAmount(order.getFeeAmount());
                    orderDetailDTO.setPaymentStatus(order.getPaymentStatus());
                    orderDetailDTO.setComment(order.getComment());
                    orderDetailDTO.setNumberOfGuest(order.getNumberOfGuest());
                    orderDetailDTO.setTable(order.getTable().toCommonCodeDTO());
                    orderDetailDTO.setHall(order.getTable().getHall().toCommonDTO());
                    orderDetailDTO.setTotalVat(order.getTotalVat());
                    orderDetailDTO.setItems(
                            order
                                    .getItems()
                                    .stream()
                                    .map(orderItem -> {
                                        OrderItemDetailAdminDTO orderItemDetailDTO = new OrderItemDetailAdminDTO();
                                        orderItemDetailDTO.setId(orderItem.getId());
                                        orderItemDetailDTO.setMenuItem(toMenuItemDetailDTO(orderItem.getMenuItem()));
                                        orderItemDetailDTO.setMultiUnit(toMultiUnitListDTO(orderItem.getMultiUnit()));
                                        orderItemDetailDTO.setQty(orderItem.getQty());
                                        orderItemDetailDTO.setPrice(orderItem.getPrice());
                                        orderItemDetailDTO.setTotalPrice(orderItem.getTotalPrice());
                                        orderItemDetailDTO.setComment(orderItem.getComment());
                                        orderItemDetailDTO.setStatus(orderItem.getStatus());
                                        orderItemDetailDTO.setMarks(orderItem.getMarks());
                                        return orderItemDetailDTO;
                                    })
                                    .collect(Collectors.toList())
                    );
                    return orderDetailDTO;
                })
                .orElseThrow(notFoundExceptionThrow(Order.class.getSimpleName(), "id", id));
    }

    public Page<OrderListAdminDTO> getListForAdmin(OrderFilter filter) {
        ResultList<Order> resultList = orderRepository.getResultList(filter);
        List<OrderListAdminDTO> result = resultList
                .getList()
                .stream()
                .map(order -> {
                    OrderListAdminDTO orderListDTO = new OrderListAdminDTO();
                    orderListDTO.setId(order.getId());
                    orderListDTO.setOrderNumber(order.getOrderNumber());
                    orderListDTO.setOrderDate(order.getOrderDate());
                    orderListDTO.setStatus(order.getStatus());
                    orderListDTO.setTotalPrice(order.getTotalPrice());
                    orderListDTO.setTotalQty(order.getTotalQty());
                    orderListDTO.setNumberOfGuest(order.getNumberOfGuest());
                    orderListDTO.setTable(order.getTable().toCommonCodeDTO());
                    orderListDTO.setClient(order.clientCommonDTO());
                    if (order.getWaiterId() != null) {
                        orderListDTO.setWaiter(order.getWaiter().toCommonDTO());
                    }
                    return orderListDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    public OrderDetailCabinetDTO getForCabinet(Long id) {
        return orderRepository
                .findById(id)
                .map(order -> {
                    OrderDetailCabinetDTO orderDetailDTO = new OrderDetailCabinetDTO();
                    orderDetailDTO.setId(order.getId());
                    orderDetailDTO.setUid(order.getUid());
                    orderDetailDTO.setOrderNumber(order.getOrderNumber());
                    orderDetailDTO.setOrderDate(order.getOrderDate());
                    if (order.getWaiterId() != null) {
                        orderDetailDTO.setWaiter(order.getWaiter().toCommonDTO());
                    }
                    orderDetailDTO.setBranch(order.getBranch().toCommonDTO());
                    orderDetailDTO.setStatus(order.getStatus());
                    orderDetailDTO.setTotalPrice(order.getTotalPrice());
                    orderDetailDTO.setTotalQty(order.getTotalQty());
                    orderDetailDTO.setFeePercent(order.getFeePercent());
                    orderDetailDTO.setFeeAmount(order.getFeeAmount());
                    orderDetailDTO.setPaymentStatus(order.getPaymentStatus());
                    orderDetailDTO.setComment(order.getComment());
                    orderDetailDTO.setNumberOfGuest(order.getNumberOfGuest());
                    orderDetailDTO.setTable(order.getTable().toCommonCodeDTO());
                    orderDetailDTO.setHall(order.getTable().getHall().toCommonDTO());
                    orderDetailDTO.setItemsCount(order.getItemsCount());
                    orderDetailDTO.setTotalVat(order.getTotalVat());
                    if (order.getClientId() != null) {
                        orderDetailDTO.setClient(new CustomerDetailDTO(order.clientCommonDTO(), order.getClient().getPhone()));
                    }
                    if (order.getReceiptId() != null) {
                        orderDetailDTO.setReceipt(order.getReceipt().toCommonDTO());
                        orderDetailDTO.setTotalPaid(order.getReceipt().getTotalPaid());
                    }
                    if (order.getShiftId() != null) {
                        orderDetailDTO.setShift(order.getShift().toCommonDTO());
                    }
                    orderDetailDTO.setItems(
                            order
                                    .getItems()
                                    .stream()
                                    .map(orderItem -> {
                                        OrderItemDetailCabinetDTO orderItemDetailDTO = new OrderItemDetailCabinetDTO();
                                        orderItemDetailDTO.setId(orderItem.getId());
                                        orderItemDetailDTO.setOrder(order.toCommonDTO());
                                        orderItemDetailDTO.setMenuItem(toMenuItemDetailDTO(orderItem.getMenuItem()));
                                        orderItemDetailDTO.setMultiUnit(toMultiUnitListDTO(orderItem.getMultiUnit()));
                                        orderItemDetailDTO.setQty(orderItem.getQty());
                                        orderItemDetailDTO.setPrice(orderItem.getPrice());
                                        orderItemDetailDTO.setTotalPrice(orderItem.getTotalPrice());
                                        orderItemDetailDTO.setComment(orderItem.getComment());
                                        orderItemDetailDTO.setStatus(orderItem.getStatus());
                                        orderItemDetailDTO.setLastModifiedDate(orderItem.getUpdatedDate());
                                        orderItemDetailDTO.setVatRate(orderItem.getVatRate());
                                        orderItemDetailDTO.setVatPrice(orderItem.getVatPrice());

                                        if (orderItem.getMenuItem().getProduct() != null) {
                                            orderItemDetailDTO.setHasMark(orderItem.getMenuItem().getProduct().isHasMark());
                                        }
                                        if (orderItem.getCreatorId() != null) {
                                            orderItemDetailDTO.setCreator(orderItem.getCreator().toCommonDTO());
                                        }
                                        if (orderItem.getUpdaterId() != null) {
                                            orderItemDetailDTO.setUpdater(orderItem.getUpdater().toCommonDTO());
                                        }
                                        if (orderItem.getOrder().getTable() != null) {
                                            if (order.getTable().getHall() != null) {
                                                orderItemDetailDTO.setHall(orderItem.getOrder().getTable().getHall().toCommonDTO());
                                            }
                                            orderItemDetailDTO.setTable(orderItem.getOrder().getTable().toCommonDTO());
                                        }
                                        orderItemDetailDTO.setMarks(orderItem.getMarks());
                                        return orderItemDetailDTO;
                                    })
                                    .collect(Collectors.toList())
                    );
                    if (!CollectionUtils.isEmpty(order.getBills())) {
                        orderDetailDTO.setBills(order.getBills().stream().map(bill -> new BillListDTO(bill.getId(), bill.getBillId(), bill.getStatus(), bill.getCreatedDate(), bill.getEpsQrCode(), bill.getAmount())).collect(Collectors.toList()));
                    }
                    return orderDetailDTO;
                })
                .orElseThrow(notFoundExceptionThrow(Order.class.getSimpleName(), "id", id));
    }

    public Page<OrderListCabinetDTO> getListForCabinet(OrderFilter filter) {
        ResultList<Order> resultList = orderRepository.getResultList(filter);
        List<OrderListCabinetDTO> result = resultList
                .getList()
                .stream()
                .map(order -> {
                    OrderListCabinetDTO orderListDTO = new OrderListCabinetDTO();
                    orderListDTO.setId(order.getId());
                    orderListDTO.setOrderNumber(order.getOrderNumber());
                    orderListDTO.setOrderDate(order.getOrderDate());
                    if (OrderStatus.CLOSED.equals(order.getStatus())) {
                        orderListDTO.setClosedDate(order.getUpdatedDate());
                    }
                    orderListDTO.setStatus(order.getStatus());
                    orderListDTO.setTotalQty(order.getTotalQty());
                    orderListDTO.setNumberOfGuest(order.getNumberOfGuest());
                    orderListDTO.setTable(order.getTable().toCommonCodeDTO());
                    orderListDTO.setTotalPrice(order.getTotalPrice());
                    orderListDTO.setTotalVat(order.getTotalVat());
                    orderListDTO.setHall(order.getTable().getHall().toCommonDTO());

                    if (order.getShiftId() != null) {
                        orderListDTO.setShift(order.getShift().toCommonDTO());
                    }
                    orderListDTO.setClient(order.clientCommonDTO());
                    if (order.getWaiterId() != null) {
                        orderListDTO.setWaiter(order.getWaiter().toCommonDTO());
                    }
                    orderListDTO.setFeePercent(order.getFeePercent());
                    orderListDTO.setFeeAmount(order.getFeeAmount());
                    return orderListDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    public OrderDetailMobileDTO getForMobile(Long id) {
        return orderRepository
                .findById(id)
                .map(order -> {
                    OrderDetailMobileDTO orderDetailDTO = new OrderDetailMobileDTO();
                    orderDetailDTO.setId(order.getId());
                    orderDetailDTO.setOrderNumber(order.getOrderNumber());
                    orderDetailDTO.setOrderDate(order.getOrderDate());

                    orderDetailDTO.setStatus(order.getStatus());
                    orderDetailDTO.setReason(order.getReason());
                    orderDetailDTO.setTotalPrice(order.getTotalPrice());
                    orderDetailDTO.setTotalQty(order.getTotalQty());
                    orderDetailDTO.setFeePercent(order.getFeePercent());
                    orderDetailDTO.setFeeAmount(order.getFeeAmount());
                    orderDetailDTO.setPaymentStatus(order.getPaymentStatus());
                    orderDetailDTO.setComment(order.getComment());
                    orderDetailDTO.setNumberOfGuest(order.getNumberOfGuest());
                    orderDetailDTO.setTable(order.getTable().toCommonCodeDTO());
                    orderDetailDTO.setTotalSellingPrice(order.getTotalSellingPrice());
                    orderDetailDTO.setHall(order.getTable().getHall().toCommonDTO());
                    orderDetailDTO.setTotalVat(order.getTotalVat());
                    orderDetailDTO.setItemsCount(order.getItemsCount());

                    if (order.getWaiterId() != null) {
                        orderDetailDTO.setWaiter(order.getWaiter().toCommonDTO());
                    }
                    if (order.getUpdaterId() != null) {
                        orderDetailDTO.setUpdater(order.getUpdater().toCommonDTO());
                    }
                    if (order.getClientId() != null) {
                        orderDetailDTO.setClient(new CustomerDetailDTO(order.clientCommonDTO(), order.getClient().getPhone()));
                    }
                    if (order.getReceiptId() != null) {
                        orderDetailDTO.setReceipt(order.getReceipt().toCommonDTO());
                    }
                    orderDetailDTO.setItems(
                            orderItemRepository.findAllByOrderIdAndDeletedIsFalseAndStatusNotIn(order.getId(), Stream.of(OrderItemStatus.CANCELLED).toList())
                                    .stream()
                                    .map(orderItem -> {
                                        OrderItemDetailMobileDTO orderItemDetailDTO = new OrderItemDetailMobileDTO();
                                        orderItemDetailDTO.setId(orderItem.getId());
                                        orderItemDetailDTO.setMenuItem(orderItem.getMenuItem().toCommonDTO());
                                        if (orderItem.getMenuItem().getProduct() != null) {
                                            orderItemDetailDTO.setVat(orderItem.getMenuItem().getProduct().getVat());
                                        }
                                        orderItemDetailDTO.setMultiUnit(toMultiUnitListDTO(orderItem.getMultiUnit()));
                                        orderItemDetailDTO.setQty(orderItem.getQty());
                                        orderItemDetailDTO.setPrice(orderItem.getPrice());
                                        orderItemDetailDTO.setTotalPrice(orderItem.getTotalPrice());
                                        orderItemDetailDTO.setComment(orderItem.getComment());
                                        orderItemDetailDTO.setStatus(orderItem.getStatus());
                                        orderItemDetailDTO.setOrderDate(orderItem.getOrderDate());
                                        orderItemDetailDTO.setLastModifiedDate(orderItem.getUpdatedDate());
                                        orderItemDetailDTO.setVatPrice(orderItem.getVatPrice());
                                        orderItemDetailDTO.setVatRate(orderItem.getVatRate());
                                        if (orderItem.getMenuItem().getProduct() != null) {
                                            orderItemDetailDTO.setHasMark(orderItem.getMenuItem().getProduct().isHasMark());
                                        }
                                        orderItemDetailDTO.setWaiter(orderItem.getCreator().toCommonDTO());
                                        if (orderItem.getUpdaterId() != null) {
                                            orderItemDetailDTO.setUpdater(order.getUpdater().toCommonDTO());
                                        }
                                        if (orderItem.getMenuItem().getPhoto() != null) {
                                            orderItemDetailDTO.setPhoto(orderItem.getMenuItem().getPhoto().toFileDTO());
                                        }
                                        orderItemDetailDTO.setMarks(orderItem.getMarks());
                                        return orderItemDetailDTO;
                                    })
                                    .collect(Collectors.toList())
                    );
                    if (!CollectionUtils.isEmpty(order.getBills())) {
                        orderDetailDTO.setBills(order.getBills().stream().map(bill -> new BillListDTO(bill.getId(), bill.getBillId(), bill.getStatus(), bill.getCreatedDate(), bill.getEpsQrCode(), bill.getAmount())).collect(Collectors.toList()));
                    }
                    return orderDetailDTO;
                })
                .orElseThrow(notFoundExceptionThrow(Order.class.getSimpleName(), "id", id));
    }

    public Page<OrderListMobileDTO> getListForMobile(OrderFilter filter) {
        ResultList<Order> resultList = orderRepository.getResultList(filter);
        List<OrderListMobileDTO> result = resultList
                .getList()
                .stream()
                .map(order -> {
                    OrderListMobileDTO orderListDTO = new OrderListMobileDTO();
                    orderListDTO.setId(order.getId());
                    orderListDTO.setOrderNumber(order.getOrderNumber());
                    orderListDTO.setOrderDate(order.getOrderDate());
                    orderListDTO.setStatus(order.getStatus());
                    orderListDTO.setTotalPrice(order.getTotalPrice());
                    orderListDTO.setTotalQty(order.getTotalQty());
                    orderListDTO.setNumberOfGuest(order.getNumberOfGuest());
                    orderListDTO.setTable(order.getTable().toCommonCodeDTO());
                    orderListDTO.setHall(order.getTable().getHall().toCommonDTO());
                    orderListDTO.setClient(order.clientCommonDTO());
                    if (order.getWaiterId() != null) {
                        orderListDTO.setWaiter(order.getWaiter().toCommonDTO());
                    }
                    if (filter.isWithItems()) {
                        AtomicInteger readyCount = new AtomicInteger(0);
                        orderListDTO.setItems(order.getItems().stream().map(orderItem -> {
                            OrderItemDetailMobileDTO orderItemListDTO = new OrderItemDetailMobileDTO();
                            orderItemListDTO.setId(orderItem.getId());
                            orderItemListDTO.setMenuItem(orderItem.getMenuItem().toCommonDTO());
                            if (orderItem.getMenuItem().getProduct() != null) {
                                orderItemListDTO.setVat(orderItem.getMenuItem().getProduct().getVat());
                            }
                            if (OrderItemStatus.READY.equals(orderItem.getStatus())) {
                                readyCount.getAndIncrement();
                            }
                            orderItemListDTO.setMultiUnit(toMultiUnitListDTO(orderItem.getMultiUnit()));
                            orderItemListDTO.setQty(orderItem.getQty());
                            orderItemListDTO.setPrice(orderItem.getPrice());
                            orderItemListDTO.setTotalPrice(orderItem.getTotalPrice());
                            orderItemListDTO.setStatus(orderItem.getStatus());
                            return orderItemListDTO;
                        }).collect(Collectors.toList()));
                    } else {
                        orderListDTO.setReadyCount(orderItemRepository.getCount(order.getId(), OrderItemStatus.READY));
                        orderListDTO.setItemCount(order.getItemsCount());
                    }
                    return orderListDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    public OrderStatisticsDTO getStatistics(OrderFilter filter) {
        if (HeaderUtils.isWaiter()) {
            filter.setWaiterId(Optional.ofNullable(filter.getWaiterId()).orElse(getCurrentUserId()));
        }
        if (HeaderUtils.isChef()) {
            filter.setChefId(Optional.ofNullable(filter.getChefId()).orElse(getCurrentUserId()));
        }
        if (filter.getWaiterId() == null && filter.getChefId() == null) {
            return new OrderStatisticsDTO();
        }
        return orderRepository.getStatistics(filter);
    }

    public List<CommonDTO> lookUp(OrderFilter filter) {
        return orderRepository.getResultList(filter).getList().stream().map(Order::toCommonDTO).collect(Collectors.toList());
    }

    public List<OrderKanbanBoardListDTO> getKanbanBoardList(OrderFilter filter) {
        if (filter.isMine() && HeaderUtils.isChef()) {
            filter.setChefId(getCurrentUserId());
        }
        return orderRepository.getKanbanBoardList(filter);
    }

    public List<OrderKanbanBoardDetailDTO> getKanbanBoardDetails(OrderFilter filter) {
        if (filter.isMine() && HeaderUtils.isChef()) {
            if (CollectionUtils.isEmpty(filter.getStatuses()) && filter.getStatus() == null) {
                filter.setStatuses(List.of(OrderStatus.NEW.getCode(), OrderStatus.ACTIVE.getCode()));
            }
            filter.setChefId(getCurrentUserId());
        } else if (filter.isMine() && HeaderUtils.isWaiter()) {
            filter.setWaiterId(getCurrentUserId());
            if (CollectionUtils.isEmpty(filter.getItemStatuses()) && filter.getItemStatus() == null) {
                filter.setItemStatuses(List.of(OrderItemStatus.READY.getCode()));
            }
            filter.setExcludeStatuses(List.of(OrderStatus.CLOSED.getCode(), OrderStatus.CANCELLED.getCode()));
        } else if (filter.isMine() && HeaderUtils.isHostess()) {
            return new ArrayList<>();
        }
        return orderRepository.getKanbanBoardDetails(filter);
    }

    public List<OrderShortListCabinetDTO> getByTableId(Long tableId) {
        return orderRepository
                .findAllByTableIdAndDeletedIsFalseAndStatusNotIn(tableId, new ArrayList<>(EnumSet.of(OrderStatus.PENDING, OrderStatus.CANCELLED, OrderStatus.CLOSED)))
                .stream()
                .map(order -> {
                    OrderShortListCabinetDTO orderShortListDTO = new OrderShortListCabinetDTO();
                    orderShortListDTO.setId(order.getId());
                    orderShortListDTO.setName(order.getName());
                    orderShortListDTO.setStatus(order.getStatus());
                    return orderShortListDTO;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(Long id) {
        orderRepository
                .findById(id)
                .map(order -> {
                    hallItemService.updateStatus(order.getTableId(), new UpdateStatusDTO(HallItemStatus.FREE.getCode()));
                    orderRepository.deleteById(id);
                    return id;
                });
    }

    @Transactional
    public void updateNumber(Long id, String orderNumber) {
        orderRepository.findById(id).map(order -> {
            if (orderRepository.existsByBranchIdAndOrderNumberAndIdNot(order.getBranchId(), orderNumber, id)) {
                throw badRequestExceptionThrow("Order with this number " + orderNumber + " already exists").get();
            }
            order.setOrderNumber(orderNumber);
            orderRepository.save(order);
            return order;
        }).orElseThrow(notFoundExceptionThrow(Order.class.getSimpleName(), "id", id));
    }

    @Transactional
    public void assignWaiter(Long id, Long waiterId) {
        orderRepository
                .findById(id)
                .map(order -> {
                    order.setWaiterId(waiterId);
                    return orderRepository.save(order);
                })
                .orElseThrow(notFoundExceptionThrow(Order.class.getSimpleName(), "id", id));
    }

    @Transactional
    public void updateStatus(Long id, UpdateStatusDTO updateStatusDTO) {
        if (updateStatusDTO.getStatus() == null) {
            throw badRequestExceptionThrow("Status is required").get();
        }
        if (OrderStatus.getByCode(updateStatusDTO.getStatus()) == null) {
            throw badRequestExceptionThrow("Status is invalid").get();
        }
        if (StringUtils.isNotEmpty(updateStatusDTO.getReason()) && updateStatusDTO.getReason() == null) {
            throw badRequestExceptionThrow("Reason is invalid").get();
        }
        orderRepository
                .findById(id)
                .map(order -> {
                    if (OrderStatus.NEW.equals(OrderStatus.valueOf(updateStatusDTO.getStatus())) && (OrderStatus.READY.equals(order.getStatus()) || OrderStatus.CANCELLED.equals(order.getStatus()))) {
                        throw badRequestExceptionThrow("You can only update orders with status NEW, ACTIVE, PENDING").get();
                    }
                    order.setStatus(OrderStatus.valueOf(updateStatusDTO.getStatus()));
                    if (StringUtils.isNotEmpty(updateStatusDTO.getReason())) {
                        order.setReason(OrderReason.valueOf(updateStatusDTO.getReason()));
                    }
                    order.setOtherReason(updateStatusDTO.getOtherReason());
                    orderRepository.save(order);
                    if (OrderStatus.CANCELLED.equals(order.getStatus()) && !HallItemStatus.FREE.equals(order.getTable().getStatus())) {
                        HallItem table = order.getTable();
                        if (table.getOrders().stream().noneMatch(order1 -> !order1.getId().equals(order.getId()) && !EnumSet.of(OrderStatus.CLOSED, OrderStatus.CANCELLED).contains(order1.getStatus()))) {
                            hallItemService.updateStatus(order.getTableId(), new UpdateStatusDTO(HallItemStatus.ACTIVE.getCode()));
                        }
                        afterCommitExecutor.execute(() -> rabbitMqOrderProducer.sendEvent(new OrderEventPayload(order.getId(), OrderEventType.ORDER_CANCELED)));
                    }
                    if (OrderStatus.CLOSED.getCode().equals(updateStatusDTO.getStatus())) {
                        order.setStatus(OrderStatus.CLOSED);
                        if (order.getReservationId() != null) {
                            Reservation reservation = order.getReservation();
                            reservation.setStatus(ReservationStatus.CLOSED);
                            reservationRepository.save(reservation);
                        }
                    }
                    orderRepository.save(order);
                    return id;
                })
                .orElseThrow(notFoundExceptionThrow(Order.class.getSimpleName(), "id", id));
    }

    @Transactional
    public Long updateComment(Long id, OrderItemCommentDTO orderItemCommentDTO) {
        return orderRepository
                .findById(id)
                .map(order -> {

                    order.setComment(orderItemCommentDTO.getComment());
                    orderRepository.save(order);
                    return id;
                })
                .orElseThrow(notFoundExceptionThrow(Order.class.getSimpleName(), "id", id));
    }

    @Transactional
    public void transfer(List<Long> orderIds, Long tableId) {
        if (!CollectionUtils.isEmpty(orderIds)) {
            List<HallItem> hallItems = new ArrayList<>();
            for (Long orderId : orderIds) {
                orderRepository
                        .findById(orderId)
                        .map(order -> {
                            HallItem oldTable = order.getTable();
                            HallItem table = hallItemRepository
                                    .findById(tableId)
                                    .orElseThrow(notFoundExceptionThrow(HallItem.class.getSimpleName(), "id", tableId));
                            order.setTableId(tableId);
                            order.setTable(table);
                            table.setStatus(HallItemStatus.ACTIVE);
                            table.setWithOrder(true);
                            calculateFeeAmount(order);
                            orderRepository.save(order);
                            hallItems.addAll(List.of(oldTable, table));
                            return order;
                        })
                        .orElseThrow(notFoundExceptionThrow(Order.class.getSimpleName(), "id", orderId));
                hallItemRepository.saveAll(hallItems);
            }
        }
    }

    @Transactional
    public Long addItem(Long orderId, List<OrderItemMobileDTO> items) {
        try {
            log.info("============================== Mobile order ADD ITEM =================== \n{}", objectMapper.writeValueAsString(items));
        } catch (JsonProcessingException e) {
            log.error("Object writing error: {}", e.getMessage());
        }
        List<Long> createdItems = new ArrayList<>();
        orderRepository.findById(orderId).map(order -> {
                    items.forEach(orderItemDTO -> {
                        MenuItem menuItem = menuItemRepository.findById(orderItemDTO.getMenuItemId()).orElseThrow(notFoundExceptionThrow(MenuItem.class.getSimpleName(), "id", orderItemDTO.getMenuItemId()));
                        OrderItem orderItem = order.getItems().stream().filter(item -> item.getMenuItemId().equals(orderItemDTO.getMenuItemId()) && item.getMultiUnitId().equals(orderItemDTO.getMultiUnitId()) && OrderItemStatus.NEW.equals(item.getStatus())).findFirst().orElseGet(OrderItem::new);
                        if (orderItem.getId() != null) {
                            this.subtract(order, orderItem);
                        } else {
                            orderItem.setMenuItemId(orderItemDTO.getMenuItemId());
                            orderItem.setComment(orderItemDTO.getComment());
                            orderItem.setMultiUnitId(orderItemDTO.getMultiUnitId());
                            orderItem.setPrice(orderItemDTO.getPrice());
                            orderItem.setOrderId(orderId);
                        }
                        if (!CollectionUtils.isEmpty(orderItemDTO.getMarks())) {
                            if (menuItem.getProduct() != null && StringUtils.isEmpty(menuItem.getProduct().getBarcode())) {
                                throw badRequestExceptionThrow("Product barcode is null").get();
                            }
                            orderItemDTO.getMarks().forEach(code -> {
                                if (MarkUtils.isNotMark(code)) {
                                    throw badRequestExceptionThrow(localizationService.localize("incorrect.product")).get();
                                }
                                if (!code.contains(menuItem.getProduct().getBarcode())) {
                                    throw badRequestExceptionThrow(localizationService.localize("incorrect.product")).get();
                                }
                                orderItem.addMark(MarkUtils.clearCode(code));
                            });
                        }
                        orderItem.setTotalPrice(Optional.ofNullable(orderItem.getTotalPrice()).orElse(BigDecimal.ZERO).add(orderItemDTO.getTotalPrice()));
                        orderItem.setQty(Optional.ofNullable(orderItem.getQty()).orElse(BigDecimal.ZERO).add(orderItemDTO.getQty()));
                        this.add(order, orderItem);
                        orderItemRepository.save(orderItem);
                        createdItems.add(orderItem.getId());
                    });
                    order.setStatus(OrderStatus.ACTIVE);
                    order.setItemsCount(Optional.ofNullable(order.getItemsCount()).orElse(0) + items.size());
                    calculateFeeAmount(order);
                    orderRepository.save(order);
                    return order.getId();
                })
                .orElseThrow(notFoundExceptionThrow(Order.class.getSimpleName(), "id", orderId));
        afterCommitExecutor.execute(() -> rabbitMqOrderProducer.sendEvent(new OrderEventPayload(orderId, createdItems, OrderEventType.ITEM_CREATE)));
        return orderId;
    }

    public void subtract(Order order, OrderItem orderItem) {
        order.setTotalQty(order.getTotalQty().subtract(orderItem.getQty()));
        order.setTotalPrice(order.getTotalPrice().subtract(orderItem.getTotalPrice()));
        BigDecimal totalSellingPrice = order.getTotalSellingPrice().subtract(orderItem.getTotalPrice());
        order.setTotalSellingPrice(totalSellingPrice);
    }

    public void add(Order order, OrderItem orderItem) {
        order.setTotalQty(order.getTotalQty().add(orderItem.getQty()));
        order.setTotalPrice(order.getTotalPrice().add(orderItem.getTotalPrice()));
        BigDecimal totalSellingPrice = order.getTotalSellingPrice().add(orderItem.getTotalPrice());
        order.setTotalSellingPrice(totalSellingPrice);
    }


}
