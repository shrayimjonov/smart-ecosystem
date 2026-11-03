package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.dto.order.request.ServiceOrderDTO;
import uz.uzkassa.cts.dto.serviceorder.response.ServiceOrderListResponseDto;
import uz.uzkassa.cts.dto.serviceorder.response.ServiceOrderResponseDto;
import uz.uzkassa.cts.filter.ServiceOrderFilter;
import uz.uzkassa.cts.service.ServiceOrderService;

@RestController
@RequestMapping("/api/service/orders")
@Tag(name = "Service Order")
@Slf4j
public class ServiceOrderResource {

    private final ServiceOrderService service;

    public ServiceOrderResource(ServiceOrderService orderService) {
        this.service = orderService;
    }

    @Operation(summary ="Add New Order")
    @PostMapping
    public ResponseEntity<ServiceOrderDTO> create(@Valid @RequestBody ServiceOrderDTO serviceOrderDTO) {
        log.debug("REST request to create new ServiceOrderDTO: {}", serviceOrderDTO);
        return ResponseEntity.ok(service.create(serviceOrderDTO));
    }

    @Operation(summary = "Update order")
    @PutMapping
    public ResponseEntity<ServiceOrderDTO> update(@Valid @RequestBody ServiceOrderDTO dto) {
        log.debug("REST request to update OrderDto : {}", dto);
        ServiceOrderDTO result = service.update(dto);
        return ResponseEntity.ok(result);
    }

    @Operation(summary ="Get order")
    @GetMapping("/{id}")
    public ResponseEntity<ServiceOrderResponseDto> getById(@PathVariable Long id) {
        log.debug("REST request to get CompanyDTO : {}", id);
        return ResponseEntity.ok(service.getById(id));
    }

    @Operation(summary ="List order")
    @GetMapping("/list")
    public ResponseEntity<Page<ServiceOrderListResponseDto>> getAll(ServiceOrderFilter filter) {
        log.debug("REST request to get all BaseFilter : {}", filter);
        return ResponseEntity.ok(service.list(filter, null));
    }


    @Operation(summary = "delete order")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request to to delete Order by id : {}", id);
        service.deleteById(id);
        return ResponseEntity.ok().build();
    }

}
