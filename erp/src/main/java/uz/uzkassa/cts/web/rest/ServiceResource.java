package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.ctsservice.request.ServiceRequestDto;
import uz.uzkassa.cts.dto.ctsservice.response.ServiceResponseDto;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.service.CtsServiceService;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@Tag(name = "Services")
@Slf4j
public class ServiceResource {

    private final CtsServiceService service;

    public ServiceResource(CtsServiceService serviceService) {
        this.service = serviceService;
    }

    @Operation(summary ="Add")
    @PostMapping
    public ResponseEntity<ServiceResponseDto> create(@Valid @RequestBody ServiceRequestDto dto) {
        log.debug("REST request to create TariffDto: {}", dto);
        return ResponseEntity.ok(service.create(dto));
    }

    @Operation(summary = "Update")
    @PutMapping
    public ResponseEntity<ServiceResponseDto> update(@Valid @RequestBody ServiceRequestDto dto) {
        log.debug("REST request to update TariffDto: {}", dto);
        return ResponseEntity.ok(service.update(dto));
    }

    @Operation(summary = "Get")
    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponseDto> getById(@PathVariable Long id) {
        log.debug("REST request to update TariffDto: {}", id);
        return ResponseEntity.ok(service.get(id));
    }

    @Operation(summary = "List")
    @GetMapping("/list")
    public ResponseEntity<Page<ServiceResponseDto>> list(@Valid BaseFilter filter) {
        log.debug("REST request to update TariffDto: {}", filter.getOrderBy());
        return ResponseEntity.ok(service.list(filter));
    }

    @Operation(summary = "Look Up")
    @GetMapping("/lookup")
    public ResponseEntity<Page<CommonDTO>> lookUp(BaseFilter filter) {
        log.debug("REST request to look up services: {}", filter);
        return ResponseEntity.ok(service.lookUp(filter));
    }

    @Operation(summary = "One Time List")
    @GetMapping("/one-time/list")
    public ResponseEntity<List<ServiceResponseDto>> oneTimeList(@Valid BaseFilter filter) {
        log.debug("REST request to update TariffDto: {}", filter.getOrderBy());
        return ResponseEntity.ok(service.oneTimeList(filter));
    }

    @Operation(summary = "Not Paid List")
    @GetMapping("/listnotpaged")
    public ResponseEntity<List<ServiceResponseDto>> list() {
        log.debug("REST request to get full list:");
        return ResponseEntity.ok(service.listNotPaged());
    }

    @Operation(summary ="Delete")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request to delete service : {}", id);
        service.customDeleteById(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Billing services")
    @GetMapping("/billing-services")
    public ResponseEntity<List<CommonDTO>> getBillingServices() {
        log.debug("REST request to get full list of billing services:");
        return ResponseEntity.ok(service.getBillingServices());
    }
}
