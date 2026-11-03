package uz.uzkassa.smartpossupply.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.template.NotificationTemplateDTO;
import uz.uzkassa.smartpossupply.dto.template.NotificationTemplateDetailDTO;
import uz.uzkassa.smartpossupply.dto.template.NotificationTemplateListDTO;
import uz.uzkassa.smartpossupply.enums.TemplateType;
import uz.uzkassa.smartpossupply.filters.TemplateFilter;
import uz.uzkassa.smartpossupply.service.NotificationTemplateService;

import java.util.EnumSet;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 06.02.2023 10:27
 */
@RestController
@RequestMapping(ApiConstants.adminNotificationTemplateRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminNotificationTemplateResource {

    NotificationTemplateService notificationTemplateService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody NotificationTemplateDTO notificationTemplateDTO) {
        return ResponseEntity.ok(notificationTemplateService.create(notificationTemplateDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody NotificationTemplateDTO notificationTemplateDTO) {
        return ResponseEntity.ok(notificationTemplateService.update(id, notificationTemplateDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<NotificationTemplateDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(notificationTemplateService.get(id));
    }

    @GetMapping
    public ResponseEntity<Page<NotificationTemplateListDTO>> getList(@ParameterObject TemplateFilter filter) {
        return ResponseEntity.ok(notificationTemplateService.getList(filter));
    }

    @GetMapping(ApiConstants.types)
    public ResponseEntity<EnumSet<TemplateType>> getTemplateTypes() {
        return ResponseEntity.ok(EnumSet.allOf(TemplateType.class));
    }
}
