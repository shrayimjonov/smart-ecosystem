package uz.uzkassa.silen.rest;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.silen.dto.NotificationCreateDTO;
import uz.uzkassa.silen.dto.NotificationDTO;
import uz.uzkassa.silen.dto.SelectItem;
import uz.uzkassa.silen.dto.filter.NotificationFilter;
import uz.uzkassa.silen.enumeration.NotificationType;
import uz.uzkassa.silen.service.NotificationService;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationResource {

    NotificationService notificationService;

    @PostMapping
    public ResponseEntity<Set<String>> create(@RequestBody NotificationCreateDTO dto) {
        return ResponseEntity.ok(notificationService.createNotificationFromTemplate(dto));
    }

    @PostMapping("/billing")
    public ResponseEntity<Set<String>> fromBilling(@RequestBody NotificationCreateDTO dto) {
        return ResponseEntity.ok(notificationService.createNotificationFromBilling(dto));
    }

    @PostMapping("/{id}")
    public ResponseEntity<Void> see(@PathVariable("id") String id) {
        notificationService.seeNotification(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<NotificationDTO>> findAll(@ModelAttribute NotificationFilter filter) {
        Page<NotificationDTO> result = notificationService.findAllByFilter(filter);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/types")
    public ResponseEntity<List<SelectItem>> types() {
        return ResponseEntity.ok(NotificationType.getAll());
    }

    @MessageMapping("/notifications")
    public void notSeenNotifications() {
        notificationService.notReadNotification();
    }

    @MessageMapping("/see")
    public void seeNotificationSocket(@Payload String id) {
        notificationService.seeNotification(id);
    }
}
