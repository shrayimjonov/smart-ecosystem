package uz.uzkassa.silen.rest;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.silen.dto.NotificationTemplateDTO;
import uz.uzkassa.silen.dto.SelectItem;
import uz.uzkassa.silen.dto.filter.NotificationFilter;
import uz.uzkassa.silen.enumeration.NotificationStatus;
import uz.uzkassa.silen.enumeration.NotificationTemplateType;
import uz.uzkassa.silen.service.NotificationTemplateService;

import java.util.List;

@RestController
@RequestMapping("/api/notification/template")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationTemplateResource {
    NotificationTemplateService notificationTemplateService;

    @PostMapping
    public ResponseEntity<NotificationTemplateDTO> create(@RequestBody NotificationTemplateDTO dto) {
        return ResponseEntity.ok(notificationTemplateService.save(dto));
    }

    @PutMapping
    public ResponseEntity<NotificationTemplateDTO> update(@RequestBody NotificationTemplateDTO dto) {
        return ResponseEntity.ok(notificationTemplateService.save(dto));
    }

    @GetMapping
    public ResponseEntity<Page<NotificationTemplateDTO>> findAll(@ModelAttribute NotificationFilter filter) {
        return ResponseEntity.ok(notificationTemplateService.findAll(filter));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationTemplateDTO> get(@PathVariable String id) {
        return ResponseEntity.ok(notificationTemplateService.findOne(id));
    }

    @GetMapping("/status")
    public ResponseEntity<List<SelectItem>> statusList() {
        return ResponseEntity.ok(NotificationStatus.getAll());
    }

    @GetMapping("/types")
    public ResponseEntity<List<SelectItem>> types() {
        return ResponseEntity.ok(NotificationTemplateType.getAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        notificationTemplateService.delete(id);
        return ResponseEntity.ok().build();
    }
}
