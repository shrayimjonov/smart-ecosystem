package uz.uzkassa.smartpos.web.rest.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.dto.notification.AdminNotificationDto;
import uz.uzkassa.smartpos.dto.notification.NotificationCreateDto;
import uz.uzkassa.smartpos.enums.BillingType;
import uz.uzkassa.smartpos.enums.NotificationCategory;
import uz.uzkassa.smartpos.enums.NotificationTarget;
import uz.uzkassa.smartpos.enums.NotificationType;
import uz.uzkassa.smartpos.filter.NotificationFilter;
import uz.uzkassa.smartpos.security.AuthoritiesConstants;
import uz.uzkassa.smartpos.service.NotificationService;

@RequestMapping("/api/v1/admin/notifications")
@RestController
@RequiredArgsConstructor
public class AdminNotificationResource {
    private final NotificationService notificationService;

    @Secured(AuthoritiesConstants.SMARTPOS_ADMIN)
    @PostMapping
    public Long create(@Valid @RequestBody NotificationCreateDto dto) {
        return notificationService.create(dto);
    }

    @Secured(AuthoritiesConstants.SMARTPOS_ADMIN)
    @PutMapping("/{id}")
    public void update(@PathVariable Long id, @Valid @RequestBody NotificationCreateDto dto) {
        notificationService.update(id, dto);
    }

    @GetMapping("/{id}")
    public AdminNotificationDto getById(@PathVariable Long id) {
        return notificationService.getById(id);
    }

    @Secured(AuthoritiesConstants.SMARTPOS_ADMIN)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        notificationService.delete(id);
    }

    @GetMapping
    public Page<AdminNotificationDto> findAll(@ParameterObject NotificationFilter filter) {
        return notificationService.findAllForAdmin(filter);
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

    @GetMapping("/billing-types")
    public BillingType[] billingTypes() {
        return BillingType.values();
    }
}
