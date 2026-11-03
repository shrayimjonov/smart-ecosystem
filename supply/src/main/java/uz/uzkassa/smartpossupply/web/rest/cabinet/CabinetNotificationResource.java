package uz.uzkassa.smartpossupply.web.rest.cabinet;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.notification.NotificationDetailDTO;
import uz.uzkassa.smartpossupply.dto.notification.NotificationListDTO;
import uz.uzkassa.smartpossupply.filters.NotificationFilter;
import uz.uzkassa.smartpossupply.service.NotificationService;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 07.02.2023 11:40
 */
@RestController
@RequestMapping(ApiConstants.cabinetNotificationRootApi)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetNotificationResource {

    NotificationService notificationService;

    @GetMapping(ApiConstants.id)
    public ResponseEntity<NotificationDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.get(id));
    }

    @GetMapping
    public ResponseEntity<Page<NotificationListDTO>> getList(@ParameterObject NotificationFilter filter) {
        return ResponseEntity.ok(notificationService.getList(filter));
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getCount(@ParameterObject NotificationFilter filter) {
        return ResponseEntity.ok(notificationService.getCount(filter));
    }
}
