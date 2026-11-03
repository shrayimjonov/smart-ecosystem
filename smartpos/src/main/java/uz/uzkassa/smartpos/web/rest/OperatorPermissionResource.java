package uz.uzkassa.smartpos.web.rest;


import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.dto.operatorpermission.request.OperatorPermissionActivateRequestDto;
import uz.uzkassa.smartpos.dto.operatorpermission.request.OperatorPermissionCreateRequestDto;
import uz.uzkassa.smartpos.dto.operatorpermission.response.OperatorPermissionResponseDto;
import uz.uzkassa.smartpos.service.OperatorPermissionService;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping("/api/operator-permission")
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Slf4j
public class OperatorPermissionResource {

    private final OperatorPermissionService service;

    public OperatorPermissionResource(OperatorPermissionService service) {
        this.service = service;
    }

    @PostMapping("/create")
    public ResponseEntity<OperatorPermissionResponseDto> create(@RequestBody OperatorPermissionCreateRequestDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PostMapping("/activate")
    public ResponseEntity<OperatorPermissionResponseDto> create(@RequestBody OperatorPermissionActivateRequestDto dto) {
        return ResponseEntity.ok(service.activate(dto));
    }

    @GetMapping("/get-last-by-company/{id}")
    public ResponseEntity<OperatorPermissionResponseDto> getLastByCompanyId(@PathVariable Long id) {
        return ResponseEntity.ok(service.getLastByCompanyId(id));
    }

    @GetMapping("/{companyId}")
    public ResponseEntity<OperatorPermissionResponseDto> getOperatorPermissionsByCompanyId(@PathVariable Long companyId, @RequestParam(required = false) Long userId) {
        return ResponseEntity.ok(service.getOperatorPermissionsByCompanyId(companyId, userId));
    }
}
