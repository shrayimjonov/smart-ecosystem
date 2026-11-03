package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.dto.accessorymodel.request.AccessoryModelCreateRequestDto;
import uz.uzkassa.cts.dto.accessorymodel.request.AccessoryModelUpdateRequestDto;
import uz.uzkassa.cts.dto.accessorymodel.response.AccessoryModelResponseDto;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.service.AccessoryModelService;

@RestController
@RequestMapping("/api/accessory-model")
@Tag(name = "Accessory model")
@Slf4j
@Deprecated
public class AccessoryModelResource {

    private final AccessoryModelService service;

    public AccessoryModelResource(AccessoryModelService service) {
        this.service = service;
    }

    @Operation(summary ="List")
    @GetMapping
    public ResponseEntity<Page<AccessoryModelResponseDto>> list(BaseFilter filter) {
        return ResponseEntity.ok(service.list(filter));
    }

    @Operation(summary ="Get")
    @GetMapping("/{id}")
    public ResponseEntity<AccessoryModelResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @Operation(summary ="Create")
    @PostMapping
    public ResponseEntity<AccessoryModelResponseDto> create(@Valid @RequestBody AccessoryModelCreateRequestDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @Operation(summary ="Update")
    @PutMapping
    public ResponseEntity<AccessoryModelResponseDto> update(@Valid @RequestBody AccessoryModelUpdateRequestDto dto) {
        return ResponseEntity.ok(service.update(dto));
    }
}
