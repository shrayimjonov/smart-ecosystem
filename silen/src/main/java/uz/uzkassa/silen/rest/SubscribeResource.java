package uz.uzkassa.silen.rest;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.silen.dto.ManagerListDTO;
import uz.uzkassa.silen.dto.SubscribeDTO;
import uz.uzkassa.silen.service.SubscribeService;

import java.util.Optional;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SubscribeResource {
    SubscribeService subscribeService;

    @PostMapping("/subscribe/to/manager")
    public ResponseEntity<Void> subscribeOrganizationsToManager(@RequestBody @Valid SubscribeDTO dto) {
        subscribeService.subscribeOrganizationsToManager(dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/subscribe/to/supervisor")
    public ResponseEntity<Void> subscribeManagersToSupervisor(@RequestBody @Valid SubscribeDTO dto) {
        subscribeService.subscribeManagersToSupervisor(dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/unsubscribe/from/manager/{organizationId}")
    public ResponseEntity<Void> unsubscribeOrganizationFromManager(@PathVariable("organizationId") String organizationId) {
        subscribeService.unsubscribeOrganizationFromManager(organizationId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/unsubscribe/from/supervisor/{managerId}")
    public ResponseEntity<Void> unsubscribeManagerFromSupervisor(@PathVariable("managerId") String managerId) {
        subscribeService.unsubscribeManagerFromSupervisor(managerId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/organization/manager/{tin}")
    ResponseEntity<Optional<ManagerListDTO>> getManagerByOrganizationTin(@PathVariable String tin) {
        return ResponseEntity.ok(Optional.of(subscribeService.getManagerByOrganizationTin(tin)));
    }
}
