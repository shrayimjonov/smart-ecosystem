package uz.uzkassa.smartpos.web.rest.fashion;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.dto.fashion.ProductReservationDetailDto;
import uz.uzkassa.smartpos.dto.fashion.ProductReservationDto;
import uz.uzkassa.smartpos.dto.fashion.ProductReservationListDto;
import uz.uzkassa.smartpos.dto.filter.ProductReservationFilter;
import uz.uzkassa.smartpos.enums.ReservationStatus;
import uz.uzkassa.smartpos.service.fashion.ProductReservationService;

@RestController
@RequestMapping("/api/v1/fashion/product-reservation")
@RequiredArgsConstructor
public class FashionProductReservationResource {
    private final ProductReservationService productReservationService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody @Valid final ProductReservationDto dto) {
        return ResponseEntity.ok(productReservationService.create(dto));
    }

    @GetMapping
    public Page<ProductReservationListDto> findAllByFilter(@ParameterObject ProductReservationFilter filter) {
        return productReservationService.findAllByFilter(filter);
    }

    @GetMapping("/{id}")
    public ProductReservationDetailDto findById(@PathVariable("id") final Long id) {
        return productReservationService.findById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> prolong(@PathVariable("id") final Long id) {
        return ResponseEntity.ok(productReservationService.prolong(id));
    }

    @PutMapping("/{id}/cancel")
    public void cancel(@PathVariable("id") final Long id) {
        productReservationService.cancel(id);
    }

    @PutMapping("/{id}/confirm")
    public void confirm(@PathVariable("id") final Long id) {
        productReservationService.confirm(id);
    }

    @GetMapping("/statuses")
    public ReservationStatus[] statuses() {
        return ReservationStatus.values();
    }
}
