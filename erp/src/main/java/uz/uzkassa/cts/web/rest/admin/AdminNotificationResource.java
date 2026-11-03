package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.notification.NotificationDTO;
import uz.uzkassa.cts.dto.notification.NotificationListDTO;
import uz.uzkassa.cts.filter.NotificationFilter;
import uz.uzkassa.cts.service.NotificationService;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminNotificationRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminNotificationResource {

    NotificationService notificationService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody NotificationDTO notificationDTO) {
        return ResponseEntity.ok(notificationService.create(notificationDTO));
    }

    @GetMapping
    public ResponseEntity<Page<NotificationListDTO>> getList(@ParameterObject NotificationFilter filter) {
        return ResponseEntity.ok(notificationService.getList(filter));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> read(@PathVariable Long id) {
        notificationService.read(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/read-all")
    public ResponseEntity<Void> readAll() {
        notificationService.readAll();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/unread/count")
    public ResponseEntity<Long> getCountUnreadNotifications() {
        return ResponseEntity.ok(notificationService.countUnreadNotifications());
    }
}
