package uz.uzkassa.smartpos.web.rest.cabinet;

import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.dto.notification.CabinetNotificationDto;
import uz.uzkassa.smartpos.enums.NotificationCategory;
import uz.uzkassa.smartpos.enums.NotificationTarget;
import uz.uzkassa.smartpos.enums.NotificationType;
import uz.uzkassa.smartpos.filter.NotificationFilter;
import uz.uzkassa.smartpos.service.NotificationService;

import java.util.List;

@RequestMapping("/api/v1/cabinet/notifications")
@RestController
@RequiredArgsConstructor
public class CabinetNotificationResource {
    private final NotificationService notificationService;

    @GetMapping
    public List<CabinetNotificationDto> findAll(@ParameterObject NotificationFilter filter) {
        return notificationService.findAllForCabinet(filter);
    }

    @PutMapping("/mark/read")
    public void markAsRead(@NotEmpty @RequestBody List<Long> ids) {
        notificationService.markAsRead(ids);
    }

    @GetMapping("/types")
    public NotificationType[] types() {
        return NotificationType.values();
    }

    @GetMapping("/categories")
    public NotificationCategory[] categories() {
        return NotificationCategory.values();
    }

    @GetMapping("/targets")
    public NotificationTarget[] targets() {
        return NotificationTarget.values();
    }
}
