package uz.uzkassa.horeca.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.horeca.domain.company.Company;
import uz.uzkassa.horeca.domain.menu.MenuItem;
import uz.uzkassa.horeca.domain.order.Order;
import uz.uzkassa.horeca.domain.order.OrderItem;
import uz.uzkassa.horeca.dto.base.CheckStatusResponseDTO;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.dto.base.UpdateStatusDTO;
import uz.uzkassa.horeca.dto.order.cabinet.*;
import uz.uzkassa.horeca.dto.order.kanban.OrderItemListDTO;
import uz.uzkassa.horeca.dto.order.kanban.OrderItemStatisticsDTO;
import uz.uzkassa.horeca.dto.order.kanban.OrderListDTO;
import uz.uzkassa.horeca.dto.order.mobile.OrderItemChefStatsMapperDTO;
import uz.uzkassa.horeca.dto.order.mobile.OrderItemCommentDTO;
import uz.uzkassa.horeca.dto.payload.OrderEventPayload;
import uz.uzkassa.horeca.enums.OrderEventType;
import uz.uzkassa.horeca.enums.OrderItemStatus;
import uz.uzkassa.horeca.enums.OrderStatus;
import uz.uzkassa.horeca.filters.BaseFilter;
import uz.uzkassa.horeca.filters.ChefStatsFilter;
import uz.uzkassa.horeca.filters.CoreFilter;
import uz.uzkassa.horeca.filters.OrderItemFilter;
import uz.uzkassa.horeca.interfaces.Validatable;
import uz.uzkassa.horeca.rabbitmq.producer.RabbitMqOrderProducer;
import uz.uzkassa.horeca.repository.MenuItemRepository;
import uz.uzkassa.horeca.repository.OrderItemRepository;
import uz.uzkassa.horeca.repository.OrderRepository;
import uz.uzkassa.horeca.utils.HeaderUtils;
import uz.uzkassa.horeca.utils.MarkUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class OrderItemService extends BaseService implements Validatable<OrderItemCabinetDTO> {

    OrderRepository orderRepository;
    OrderItemRepository orderItemRepository;
    OrderService orderService;
    MenuItemRepository menuItemRepository;
    RabbitMqOrderProducer rabbitMqOrderProducer;

    @Override
    public void validate(OrderItemCabinetDTO orderItemDTO) {
        if (orderItemDTO.getOrderId() == null) {
            throw badRequestExceptionThrow("Order is required").get();
        }
        if (orderItemDTO.getMenuItemId() == null) {
            throw badRequestExceptionThrow("Menu item id is required").get();
        }
        if (orderItemDTO.getMultiUnitId() == null) {
            throw badRequestExceptionThrow("Multi unit id is required").get();
        }
        if (orderItemDTO.getQty() == null || orderItemDTO.getQty().compareTo(BigDecimal.ZERO) == 0) {
            throw badRequestExceptionThrow("Qty is required").get();
        }
        if (orderItemDTO.getTotalPrice() == null || orderItemDTO.getTotalPrice().compareTo(BigDecimal.ZERO) == 0) {
            throw badRequestExceptionThrow("Total price is required").get();
        }
        orderRepository.findById(orderItemDTO.getOrderId()).ifPresent(order -> {
            if (OrderStatus.CANCELLED == order.getStatus()) {
                throw badRequestExceptionThrow(localizationService.localize("order.already.closed")).get();
            }
        });
    }

    @Transactional
    public Long create(OrderItemCabinetDTO orderItemDTO) {
        this.validate(orderItemDTO);
        List<Long> createdItems = new ArrayList<>();
        Long id = orderRepository
                .findById(orderItemDTO.getOrderId())
                .map(order -> {
                    MenuItem menuItem = menuItemRepository.findById(orderItemDTO.getMenuItemId()).orElseThrow(notFoundExceptionThrow(MenuItem.class.getSimpleName(), "id", orderItemDTO.getMenuItemId()));
                    OrderItem orderItem = new OrderItem();
                    orderItem.setMenuItemId(orderItem.getMenuItemId());
                    orderItem.setMultiUnitId(orderItem.getMultiUnitId());
                    orderItem.setQty(orderItemDTO.getQty());
                    orderItem.setPrice(orderItemDTO.getPrice());
                    orderItem.setTotalPrice(orderItemDTO.getTotalPrice());
                    orderItem.setComment(orderItemDTO.getComment());
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
                    order.setTotalPrice(order.getTotalPrice().add(orderItemDTO.getTotalPrice()));
                    order.setTotalQty(order.getTotalQty().add(orderItemDTO.getQty()));
                    order.addItem(orderItem);
                    order.setItemsCount(order.getItemsCount() + 1);
                    calculateFeeAmount(order);
                    orderRepository.save(order);
                    orderItemRepository.save(orderItem);
                    createdItems.add(orderItem.getId());
                    return orderItem.getId();
                })
                .orElseThrow(notFoundExceptionThrow(OrderItem.class.getSimpleName(), "id", orderItemDTO.getOrderId()));
        afterCommitExecutor.execute(() -> rabbitMqOrderProducer.sendEvent(new OrderEventPayload(orderItemDTO.getOrderId(), createdItems, OrderEventType.ITEM_CREATE)));
        return id;
    }

    public OrderItemDetailCabinetDTO get(Long id) {
        return orderItemRepository
                .findById(id)
                .map(orderItem -> {
                    OrderItemDetailCabinetDTO orderItemDetailDTO = new OrderItemDetailCabinetDTO();
                    orderItemDetailDTO.setId(orderItem.getId());
                    orderItemDetailDTO.setOrder(orderItem.getOrder().toCommonDTO());
                    orderItemDetailDTO.setMenuItem(toMenuItemDetailDTO(orderItem.getMenuItem()));
                    orderItemDetailDTO.setMultiUnit(toMultiUnitListDTO(orderItem.getMultiUnit()));
                    orderItemDetailDTO.setQty(orderItem.getQty());
                    orderItemDetailDTO.setPrice(orderItem.getPrice());
                    orderItemDetailDTO.setTotalPrice(orderItem.getTotalPrice());
                    orderItemDetailDTO.setComment(orderItem.getComment());
                    orderItemDetailDTO.setStatus(orderItem.getStatus());
                    orderItemDetailDTO.setOrderDate(orderItem.getOrderDate());
                    orderItemDetailDTO.setLastModifiedDate(orderItem.getUpdatedDate());
                    if (orderItem.getOrder().getTable() != null) {
                        orderItemDetailDTO.setHall(orderItem.getOrder().getTable().getHall().toCommonDTO());
                        orderItemDetailDTO.setTable(orderItem.getOrder().getTable().toCommonDTO());
                    }
                    if (orderItem.getCreatorId() != null) {
                        orderItemDetailDTO.setCreator(orderItem.getCreator().toCommonDTO());
                    }
                    if (orderItem.getUpdaterId() != null) {
                        orderItemDetailDTO.setUpdater(orderItem.getUpdater().toCommonDTO());
                    }
                    if (orderItem.getMenuItem().getProduct() != null) {
                        orderItemDetailDTO.setHasMark(orderItem.getMenuItem().getProduct().isHasMark());
                    }
                    orderItemDetailDTO.setMarks(orderItem.getMarks());
                    return orderItemDetailDTO;
                })
                .orElseThrow(notFoundExceptionThrow(OrderItem.class.getSimpleName(), "id", id));
    }

    @Transactional
    public Long update(Long id, OrderItemCabinetDTO orderItemDTO) {
        orderItemDTO.setId(id);
        this.validate(orderItemDTO);
        orderItemRepository
                .findById(id)
                .map(orderItem -> {
                    Order order = orderItem.getOrder();
                    this.subtract(order, orderItem);
                    orderItem.setQty(orderItemDTO.getQty());
                    orderItem.setPrice(orderItemDTO.getPrice());
                    orderItem.setTotalPrice(orderItemDTO.getTotalPrice());
                    orderItem.setComment(orderItemDTO.getComment());
                    this.add(order, orderItem);
                    if (!CollectionUtils.isEmpty(orderItemDTO.getMarks())) {
                        if (orderItem.getMenuItem().getProduct() != null && StringUtils.isEmpty(orderItem.getMenuItem().getProduct().getBarcode())) {
                            throw badRequestExceptionThrow("Product barcode is null").get();
                        }
                        orderItemDTO.getMarks().forEach(code -> {
                            if (MarkUtils.isNotMark(code)) {
                                throw badRequestExceptionThrow(localizationService.localize("incorrect.product")).get();
                            }
                            if (!code.contains(orderItem.getMenuItem().getProduct().getBarcode())) {
                                throw badRequestExceptionThrow(localizationService.localize("incorrect.product")).get();
                            }
                        });
                        orderItem.setMarks(orderItemDTO.getMarks().stream().map(MarkUtils::clearCode).collect(Collectors.toSet()));
                    }
                    orderItemRepository.save(orderItem);
                    calculateFeeAmount(order);
                    orderRepository.save(order);
                    return orderItem.getId();
                })
                .orElseThrow(notFoundExceptionThrow(OrderItem.class.getSimpleName(), "id", id));
        afterCommitExecutor.execute(() -> rabbitMqOrderProducer.sendEvent(new OrderEventPayload(orderItemDTO.getOrderId(), Collections.singletonList(id), OrderEventType.ITEM_UPDATE)));
        return id;
    }


    @Transactional
    public CheckStatusResponseDTO delete(Long id, LocalDateTime lastModifiedDate) {
        OrderItem orderItem = orderItemRepository.findById(id).orElseThrow(notFoundExceptionThrow(OrderItem.class.getSimpleName(), "id", id));

        if (lastModifiedDate != null) {
            if (!lastModifiedDate.equals(orderItem.getUpdatedDate())) {
                return new CheckStatusResponseDTO(false, 1);
            }
        }
        if (OrderItemStatus.NEW == orderItem.getStatus()) {
            orderItem.setDeleted(true);
        } else {
            orderItem.setStatus(OrderItemStatus.CANCELLED);
        }
        orderItemRepository.save(orderItem);

        Order order = orderItem.getOrder();
        this.subtract(order, orderItem);
        order.setItemsCount(order.getItemsCount() - 1);
        calculateFeeAmount(order);
        orderRepository.save(order);

        afterCommitExecutor.execute(() -> rabbitMqOrderProducer.sendEvent(new OrderEventPayload(orderItem.getOrderId(), Collections.singletonList(orderItem.getId()), OrderEventType.ITEM_REMOVE)));
        return new CheckStatusResponseDTO(true, 0);
    }

    public List<OrderItemDetailCabinetDTO> getList(OrderItemFilter filter) {
        if (filter.isMine() && HeaderUtils.isChef()) {
            filter.setChefId(HeaderUtils.getCurrentUserId());
        }
        return orderItemRepository
                .getResultList(filter)
                .getList()
                .stream()
                .map(orderItem -> {
                    OrderItemDetailCabinetDTO orderItemDetailDTO = new OrderItemDetailCabinetDTO();
                    orderItemDetailDTO.setId(orderItem.getId());
                    orderItemDetailDTO.setMenuItem(toMenuItemDetailDTO(orderItem.getMenuItem()));
                    orderItemDetailDTO.setMultiUnit(toMultiUnitListDTO(orderItem.getMultiUnit()));
                    orderItemDetailDTO.setQty(orderItem.getQty());
                    orderItemDetailDTO.setPrice(orderItem.getPrice());
                    orderItemDetailDTO.setTotalPrice(orderItem.getTotalPrice());
                    orderItemDetailDTO.setComment(orderItem.getComment());
                    orderItemDetailDTO.setStatus(orderItem.getStatus());
                    orderItemDetailDTO.setOrderDate(orderItem.getOrderDate());
                    orderItemDetailDTO.setLastModifiedDate(orderItem.getUpdatedDate());
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
                        orderItemDetailDTO.setHall(orderItem.getOrder().getTable().getHall().toCommonDTO());
                        orderItemDetailDTO.setTable(orderItem.getOrder().getTable().toCommonDTO());
                    }
                    orderItemDetailDTO.setOrder(orderItem.getOrder().toCommonDTO());
                    orderItemDetailDTO.setMarks(orderItem.getMarks());
                    return orderItemDetailDTO;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public Long updateComment(Long id, OrderItemCommentDTO orderItemCommentDTO) {
        return orderItemRepository
                .findById(id)
                .map(orderItem -> {
                    orderItem.setComment(orderItemCommentDTO.getComment());
                    orderItemRepository.save(orderItem);
                    return orderItem.getId();
                })
                .orElseThrow(notFoundExceptionThrow(OrderItem.class.getSimpleName(), "id", id));

    }

    public CheckStatusResponseDTO updateStatus(Long id, OrderItemStatus status, LocalDateTime lastModifiedDate) {
        return orderItemRepository
                .findById(id)
                .map(orderItem -> {
                    if (lastModifiedDate != null) {
                        if (!lastModifiedDate.equals(orderItem.getUpdatedDate())) {
                            return new CheckStatusResponseDTO(false, 1);
                        }
                    }
                    orderItem.setStatus(status);
                    if (OrderItemStatus.PREPARING == status) {
                        orderItem.setChefId(getCurrentUserId());
                    }
                    if (OrderItemStatus.READY == status || OrderItemStatus.CANCELLED == status) {
                        Order order = orderItem.getOrder();
                        if (order.getItems().stream().allMatch(item -> (OrderItemStatus.READY == item.getStatus()))) {
                            orderService.updateStatus(order.getId(), new UpdateStatusDTO(OrderStatus.valueOf(status.getCode()).getCode()));
                        }
                        if (order.getItems().stream().allMatch(item -> (OrderItemStatus.CANCELLED == item.getStatus()))) {
                            orderService.updateStatus(order.getId(), new UpdateStatusDTO(OrderStatus.valueOf(status.getCode()).getCode()));
                        }
                        if (orderItem.getCreatorId() != null) {
                            OrderEventType orderEventType = OrderItemStatus.READY == status ? OrderEventType.ITEM_READY : OrderEventType.ITEM_CANCELED;
                            afterCommitExecutor.execute(() -> rabbitMqOrderProducer.sendEvent(new OrderEventPayload(orderItem.getOrderId(), Collections.singletonList(orderItem.getId()), orderEventType)));
                        }
                    }
                    if (OrderItemStatus.GIVEN == status) {
                        Order order = orderItem.getOrder();
                        List<OrderItem> oldItems = order.getItems().stream().filter(item -> !item.getId().equals(orderItem.getId()) && item.getMenuItemId().equals(orderItem.getMenuItemId()) && item.getMultiUnitId().equals(orderItem.getMultiUnitId()) && item.getStatus() == orderItem.getStatus()).toList();
                        if (!CollectionUtils.isEmpty(oldItems)) {
                            for (OrderItem oldItem : oldItems) {
                                orderItem.setQty(orderItem.getQty().add(oldItem.getQty()));
                                orderItem.setTotalPrice(orderItem.getTotalPrice().add(oldItem.getTotalPrice()));
                                orderItem.addMark(oldItem.getMarks());
                                oldItem.setDeleted(true);
                            }
                            orderItemRepository.saveAll(oldItems);
                        }
                    }
                    orderItemRepository.save(orderItem);
                    return new CheckStatusResponseDTO(true, 0);
                })
                .orElseThrow(notFoundExceptionThrow(OrderItem.class.getSimpleName(), "id", id));
    }

    public OrderItemStatisticsDTO getStatistics(OrderItemFilter filter) {
        if (HeaderUtils.isChef()) {
            filter.setChefId(Optional.ofNullable(filter.getChefId()).orElse(getCurrentUserId()));
        }
        if (HeaderUtils.isWaiter()) {
            filter.setWaiterId(Optional.ofNullable(filter.getWaiterId()).orElse(getCurrentUserId()));
        }
        return orderItemRepository.getStatistics(filter);
    }

    public OrderItemChefStatsMapperDTO getMobileChefStatistics(ChefStatsFilter filter) {
        return orderItemRepository.getMobileChefStatistics(filter);
    }

    public List<OrderListDTO> getKanbanBoardList(OrderItemFilter filter) {
        if (HeaderUtils.isChef()) {
            filter.setChefId(getCurrentUserId());
        } else if (HeaderUtils.isWaiter()) {
            filter.setWaiterId(getCurrentUserId());
        }
        ResultList<OrderItem> resultList = orderItemRepository.getResultList(filter);
        Map<Long, OrderListDTO> resultMap = new LinkedHashMap<>();
        for (OrderItem orderItem : resultList.getList()) {
            OrderListDTO orderListDTO;
            if (resultMap.get(orderItem.getOrderId()) == null) {
                orderListDTO = new OrderListDTO();
                orderListDTO.setOrder(orderItem.getOrder().toCommonDTO());
                orderListDTO.setTable(orderItem.getOrder().getTable().toCommonDTO());
                orderListDTO.setStatus(orderItem.getOrder().getStatus());
                if (orderItem.getOrder().getWaiter() != null) {
                    orderListDTO.setWaiter(orderItem.getOrder().getWaiter().toCommonDTO());
                }
                orderListDTO.setComment(orderItem.getOrder().getComment());
                resultMap.put(orderItem.getOrderId(), orderListDTO);
            } else {
                orderListDTO = resultMap.get(orderItem.getOrderId());
            }
            OrderItemListDTO itemListDTO = new OrderItemListDTO();
            itemListDTO.setId(orderItem.getId());
            itemListDTO.setMenuItem(orderItem.getMenuItem().toCommonDTO());
            itemListDTO.setStatus(orderItem.getStatus());
            itemListDTO.setOrderItemDate(orderItem.getOrderDate());
            itemListDTO.setLastModifiedDate(orderItem.getUpdatedDate());
            if (orderItem.getChef() != null) {
                itemListDTO.setChef(orderItem.getChef().toCommonDTO());
            }
            if (orderItem.getCreatorId() != null) {
                itemListDTO.setCreator(orderItem.getCreator().toCommonDTO());
            }
            if (orderItem.getUpdaterId() != null) {
                itemListDTO.setUpdater(orderItem.getUpdater().toCommonDTO());
            }
            itemListDTO.setMultiUnit(toMultiUnitListDTO(orderItem.getMultiUnit()));
            itemListDTO.setComment(orderItem.getComment());
            itemListDTO.setQty(orderItem.getQty());
            orderListDTO.addItem(itemListDTO);
        }
        return new ArrayList<>(resultMap.values());
    }

    public Long addMark(Long orderItemId, OrderItemMarkDTO markDTO) {
        return orderItemRepository.findById(orderItemId).map(orderItem -> {
                    log.info("ORDER_ITEM_ID: {}, ORDER_ITEM_COUNT: {}, ORDER_ITEM_MARK_COUNT: {}", orderItem.getId(), orderItem.getQty(), orderItem.getMarks().size());
                    if (orderItem.getMarks().size() >= Optional.ofNullable(orderItem.getQty()).orElse(BigDecimal.ZERO).longValue()) {
                        throw badRequestExceptionThrow("Отсканировано достаточное количество маркировок").get();
                    }
                    if (StringUtils.isNotEmpty(markDTO.getCode())) {
                        if (orderItem.getMarks().contains(MarkUtils.clearCode(markDTO.getCode()))) {
                            throw badRequestExceptionThrow("Code already exists").get();
                        }
                        MarkCheckResponseDTO checkResponseDTO = this.check(new MarkCheckDTO(markDTO.getCode(), orderItem.getMenuItem().getProduct().getBarcode()));
                        if (!checkResponseDTO.isSuccess()) {
                            throw badRequestExceptionThrow(checkResponseDTO.getErrorMessage()).get();
                        }
                        orderItem.getMarks().add(MarkUtils.clearCode(markDTO.getCode()));
                        orderItemRepository.save(orderItem);
                    }
                    return orderItem.getId();
                })
                .orElseThrow(notFoundExceptionThrow(OrderItem.class.getSimpleName(), "id", orderItemId));
    }

    public Long replaceMark(Long orderItemId, OrderItemMarkDTO markDTO) {
        return orderItemRepository.findById(orderItemId).map(orderItem -> {
                    log.info("ORDER_ITEM_ID: {}, ORDER_ITEM_COUNT: {}, ORDER_ITEM_MARK_COUNT: {}", orderItem.getId(), orderItem.getQty(), orderItem.getMarks().size());
                    if (StringUtils.isEmpty(markDTO.getCode())) {
                        throw badRequestExceptionThrow("Code is empty").get();
                    }
                    if (StringUtils.isEmpty(markDTO.getReplaceableCode())) {
                        throw badRequestExceptionThrow("Replaceable code is empty").get();
                    }
                    if (!orderItem.getMarks().contains(markDTO.getCode())) {
                        throw badRequestExceptionThrow("Code does not exist").get();
                    }
                    if (orderItem.getMarks().contains(MarkUtils.clearCode(markDTO.getReplaceableCode()))) {
                        throw badRequestExceptionThrow("Replaceable code already exists").get();
                    }
                    MarkCheckResponseDTO checkResponseDTO = this.check(new MarkCheckDTO(markDTO.getReplaceableCode(), orderItem.getMenuItem().getProduct().getBarcode()));
                    if (!checkResponseDTO.isSuccess()) {
                        throw badRequestExceptionThrow(checkResponseDTO.getErrorMessage()).get();
                    }
                    orderItem.getMarks().remove(markDTO.getCode());
                    orderItem.getMarks().add(MarkUtils.clearCode(markDTO.getReplaceableCode()));
                    orderItemRepository.save(orderItem);
                    return orderItem.getId();
                })
                .orElseThrow(notFoundExceptionThrow(OrderItem.class.getSimpleName(), "id", orderItemId));
    }

    public Long deleteMark(Long orderItemId, OrderItemMarkDTO markDTO) {
        return orderItemRepository.findById(orderItemId).map(orderItem -> {
                    if (StringUtils.isNotEmpty(markDTO.getCode())) {
                        if (!orderItem.getMarks().contains(MarkUtils.clearCode(markDTO.getCode()))) {
                            throw badRequestExceptionThrow("Code does not exists").get();
                        }
                        orderItem.getMarks().remove(MarkUtils.clearCode(markDTO.getCode()));
                        Order order = orderRepository.findById(orderItem.getOrderId()).orElseThrow(notFoundExceptionThrow(Order.class.getSimpleName(), "id", orderItem.getOrderId()));
                        this.subtract(order, orderItem);
                        orderItem.setTotalPrice(orderItem.getTotalPrice().subtract(orderItem.getTotalPrice().divide(orderItem.getQty(), RoundingMode.HALF_UP)));
                        orderItem.setQty(orderItem.getQty().subtract(BigDecimal.ONE));
                        this.add(order, orderItem);
                        orderItemRepository.save(orderItem);
                        orderRepository.save(order);
                    }
                    return orderItem.getId();
                })
                .orElseThrow(notFoundExceptionThrow(OrderItem.class.getSimpleName(), "id", orderItemId));
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

    public MarkCheckResponseDTO check(MarkCheckDTO markDTO) {
        if (StringUtils.isEmpty(markDTO.getBarcode())) {
            return new MarkCheckResponseDTO("Product barcode is null", false);
        }
        if (MarkUtils.isNotMark(markDTO.getCode())) {
            return new MarkCheckResponseDTO(localizationService.localize("incorrect.product"), false);
        }
        if (!markDTO.getCode().contains(markDTO.getBarcode())) {
            return new MarkCheckResponseDTO(localizationService.localize("incorrect.product"), false);
        }

        Company company = getCurrentCompany();
        List<Order> orders = orderItemRepository.findFirstOrderByCodeAndStatus(company.getId(), MarkUtils.clearCode(markDTO.getCode()), OrderStatus.CANCELLED.getCode(), OrderItemStatus.CANCELLED.getCode());
        return orders.stream().findFirst().map(value -> new MarkCheckResponseDTO("Код уже использован. Номер заказа: ".concat(value.getOrderNumber()), false)).orElse(new MarkCheckResponseDTO(true));
    }
}
