package uz.uzkassa.horeca.service;

import com.google.firebase.messaging.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.horeca.domain.Department;
import uz.uzkassa.horeca.domain.hall.HallItem;
import uz.uzkassa.horeca.domain.menu.MenuItem;
import uz.uzkassa.horeca.domain.order.Order;
import uz.uzkassa.horeca.domain.order.OrderItem;
import uz.uzkassa.horeca.dto.notification.FirebaseNotificationDTO;
import uz.uzkassa.horeca.dto.notification.FirebaseNotificationSendDTO;
import uz.uzkassa.horeca.dto.payload.OrderEventPayload;
import uz.uzkassa.horeca.enums.*;
import uz.uzkassa.horeca.repository.OrderItemRepository;
import uz.uzkassa.horeca.repository.OrderRepository;
import uz.uzkassa.horeca.repository.UserSessionRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 29.10.2024
 */
@Slf4j
@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class FirebaseNotificationService {

    FirebaseMessaging firebaseMessaging;
    UserSessionRepository userSessionRepository;
    OrderItemRepository orderItemsRepository;
    OrderRepository orderRepository;
    LocalizationService localizationService;

    private static final Map<OrderEventType, String> messageKeys = new HashMap<>();
    private static final Map<DepartmentType, Role> departmentRoles = new HashMap<>();

    static {
        messageKeys.put(OrderEventType.ORDER_CREATE, "order.created");
        messageKeys.put(OrderEventType.ORDER_UPDATE, "order.updated");
        messageKeys.put(OrderEventType.ORDER_CANCELED, "order.cancelled");
        messageKeys.put(OrderEventType.ITEM_CREATE, "order.item.created");
        messageKeys.put(OrderEventType.ITEM_UPDATE, "order.item.updated");
        messageKeys.put(OrderEventType.ITEM_REMOVE, "order.item.deleted");
        messageKeys.put(OrderEventType.ITEM_READY, "order.item.ready");
        messageKeys.put(OrderEventType.ITEM_CANCELED, "order.item.cancelled");
        departmentRoles.put(DepartmentType.KITCHEN, Role.ROLE_CHEF);
        departmentRoles.put(DepartmentType.BAR, Role.ROLE_BARMEN);
    }



    public void sendNotification(FirebaseNotificationSendDTO notificationDTO) {

        Notification notification = Notification.builder()
                .setTitle(Optional.ofNullable(notificationDTO.getTitle()).orElse("HORECA"))
                .setBody(notificationDTO.getMessage())
                .setImage(Optional.ofNullable(notificationDTO.getLogoUrl()).orElse("https://cdn.pixabay.com/photo/2017/05/14/11/33/football-2311831_150.png"))
                .build();

        Message message = Message.builder()
                .setNotification(notification)
                .setToken(notificationDTO.getRecipientToken())
                .putAllData(notificationDTO.getAdditionalProperties())
                .setApnsConfig(ApnsConfig.builder()
                        .putHeader("apns-priority","10")
                        .setAps(Aps.builder().setSound("default").build())
                        .build())
                .build();
        try {
            firebaseMessaging.send(message);
            log.info("--------------SUCCESS !!!--------------");
        } catch (FirebaseMessagingException e) {
            log.error("Firebase send notification failed.");
            log.error("Firebase could not sent notification: fcmToken: {} \n Error: {}", notificationDTO.getRecipientToken(), e.getMessage());
            log.error(e.getMessage());
        }
    }

    public void sendNotification(String message, String recipientToken, String logoUrl, Map<String, String> additionalProperties) {
        FirebaseNotificationSendDTO notificationDTO = FirebaseNotificationSendDTO.builder()
                .logoUrl(logoUrl)
                .message(message)
                .recipientToken(recipientToken)
                .additionalProperties(additionalProperties)
                .build();
        this.sendNotification(notificationDTO);
    }

    public void sendNotification(Long userId, String message, String logoUrl, Map<String, String> additionalProperties) {
        userSessionRepository.findAllByUserIdOrderByLastUpdatedDateDesc(userId)
                .forEach(session -> {
                    FirebaseNotificationSendDTO notificationDTO = FirebaseNotificationSendDTO.builder()
                            .logoUrl(logoUrl)
                            .message(message)
                            .recipientToken(session.getFcmToken())
                            .additionalProperties(additionalProperties)
                            .build();
                    this.sendNotification(notificationDTO);
                });
    }

    public void sendNotificationToUsersByDepartmentAndRole(Long departmentId, Role role, String message, String logoUrl, Map<String, String> additionalProperties) {
        userSessionRepository.findAllByUser_DepartmentIdAndUser_RoleAndUser_DeletedIsFalse(departmentId, role)
                .forEach(userSession -> this.sendNotification(message, userSession.getFcmToken(), logoUrl, additionalProperties));
    }

    public void sendNotificationToUsersByDepartment(Long departmentId, String message, String logoUrl, Map<String, String> additionalProperties) {
        userSessionRepository.findAllByUser_DepartmentIdAndUser_DeletedIsFalse(departmentId)
                .forEach(userSession -> this.sendNotification(message, userSession.getFcmToken(), logoUrl, additionalProperties));
    }

    public void sendNotification(FirebaseNotificationDTO notificationDTO) {
        if (notificationDTO.getDepartmentId() != null) {
            if (notificationDTO.getRole() != null) {
                this.sendNotificationToUsersByDepartmentAndRole(notificationDTO.getDepartmentId(), notificationDTO.getRole(), notificationDTO.getMessage(), notificationDTO.getLogoUrl(), notificationDTO.getAdditionalProperties());
            } else {
                this.sendNotificationToUsersByDepartment(notificationDTO.getDepartmentId(), notificationDTO.getMessage(), notificationDTO.getLogoUrl(), notificationDTO.getAdditionalProperties());
            }
        } else if (notificationDTO.getUserId() != null) {
            this.sendNotification(notificationDTO.getUserId(), notificationDTO.getMessage(), notificationDTO.getLogoUrl(), notificationDTO.getAdditionalProperties());
        }
    }


    public void processingOrderEvents(OrderEventPayload orderEventPayload) {
        final Order order = orderRepository.findById(orderEventPayload.getId()).orElse(null);
        if (order == null) {
            return;
        }
        final HallItem table = order.getTable();
        Map<String, String> additionalProperties = new HashMap<>(3);
        additionalProperties.put(EntityTypeEnum.HALL.getCode(), table.getHallId().toString());
        additionalProperties.put(EntityTypeEnum.HALL_ITEM.getCode(), table.getId().toString());
        additionalProperties.put(EntityTypeEnum.ORDER.getCode(), order.getId().toString());
        final String orderName = order.getName();
        final String tableName = table.getName();
        final String hallName = table.getHall() != null ? table.getHall().getName() : "";
        final FirebaseNotificationDTO notificationDTO = new FirebaseNotificationDTO();
        notificationDTO.setAdditionalProperties(additionalProperties);
        final List<OrderItem> orderItems;
        if (CollectionUtils.isEmpty(orderEventPayload.getOrderItems())) {
            orderItems = order.getItems();
        } else {
            orderItems = orderItemsRepository.findAllById(orderEventPayload.getOrderItems());
        }
        if (OrderStatus.CANCELLED.equals(order.getStatus())) {
            notificationDTO.setUserId(order.getWaiterId());
            notificationDTO.setMessage(localize(OrderEventType.ORDER_CANCELED, orderName, hallName, tableName));
            sendNotification(notificationDTO);
            for (OrderItem orderItem : orderItems) {
                if (OrderItemStatus.PREPARING.equals(orderItem.getStatus())) {
                    notificationDTO.setUserId(orderItem.getChefId());
                    MenuItem menuItem = orderItem.getMenuItem();
                    if (menuItem.getProduct() == null) {
                        continue;
                    }
                    if (orderItem.getMenuItem().getPhoto() != null) {
                        notificationDTO.setLogoUrl(menuItem.getPhoto().getUrl());
                    }
                    notificationDTO.setMessage(localize(OrderEventType.ITEM_CANCELED, orderName, hallName, tableName, menuItem.getName()));
                    sendNotification(notificationDTO);
                }
            }
        } else {
            for (OrderItem orderItem : orderItems) {
                MenuItem menuItem = orderItem.getMenuItem();
                if (menuItem.getProduct() == null) {
                    continue;
                }
                if (orderItem.getMenuItem().getPhoto() != null) {
                    notificationDTO.setLogoUrl(menuItem.getPhoto().getUrl());
                }
                if (OrderItemStatus.READY.equals(orderItem.getStatus())) {
                    notificationDTO.setUserId(orderItem.getCreatorId());
                }
                if (OrderItemStatus.NEW.equals(orderItem.getStatus())) {
                    Department department = menuItem.getProduct().getDepartment();
                    Role orDefault = departmentRoles.getOrDefault(department.getType(), Role.ROLE_CHEF);
                    notificationDTO.setDepartmentId(department.getId());
                    notificationDTO.setRole(orDefault);
                }
                notificationDTO.setMessage(localize(orderEventPayload.getType(), orderName, hallName, tableName, menuItem.getName()));
                if (OrderItemStatus.CANCELLED.equals(orderItem.getStatus())) {
                    if (orderItem.getChefId() != null) {
                        notificationDTO.setUserId(orderItem.getChefId());
                        sendNotification(notificationDTO);
                    }
                    if (order.getCreatorId() != null) {
                        notificationDTO.setUserId(orderItem.getCreatorId());
                        sendNotification(notificationDTO);
                    }
                } else {
                    sendNotification(notificationDTO);
                }
            }
        }
    }

    private String localize(OrderEventType orderEventType, Object... objects) {
        return localizationService.localize(messageKeys.getOrDefault(orderEventType, ""), objects);
    }
}
