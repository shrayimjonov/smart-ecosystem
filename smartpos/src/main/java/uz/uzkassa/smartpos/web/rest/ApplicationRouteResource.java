package uz.uzkassa.smartpos.web.rest;


import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.dto.applicationroute.request.ApplicationRouteRequestDto;
import uz.uzkassa.smartpos.dto.applicationroute.response.ApplicationRoutUserPermissions;
import uz.uzkassa.smartpos.dto.applicationroute.response.ApplicationRouteListResponseDto;
import uz.uzkassa.smartpos.dto.applicationroute.response.ApplicationRouteResponseDto;
import uz.uzkassa.smartpos.service.ApplicationRouteService;

import java.util.List;

@RestController
@RequestMapping("/api/applicationRoute")
@Slf4j
public class ApplicationRouteResource {

    private final ApplicationRouteService service;

    public ApplicationRouteResource(ApplicationRouteService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApplicationRouteResponseDto> create(@Valid @RequestBody ApplicationRouteRequestDto dto) {
        return ResponseEntity.ok().body(service.create(dto));
    }

    @PutMapping
    public ResponseEntity<ApplicationRouteResponseDto> update(@Valid @RequestBody ApplicationRouteRequestDto dto) {
        return ResponseEntity.ok().body(service.update(dto));
    }

    @GetMapping(path = {"/list/{parentId}", "/list"})
    public ResponseEntity<List<ApplicationRouteResponseDto>> list(@PathVariable(required = false) Long parentId) {
        return ResponseEntity.ok().body(service.list(parentId));
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<ApplicationRouteListResponseDto>> getAll() {
        return ResponseEntity.ok().body(service.getAll());
    }

    @GetMapping("/get-all/{userId}")
    public ResponseEntity<List<ApplicationRoutUserPermissions>> getUserPermissionsList(@PathVariable(required = false) Long userId) {
        return ResponseEntity.ok().body(service.getAllWithUserPermission(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApplicationRouteResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok().body(service.getById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.ok().build();
    }

}
