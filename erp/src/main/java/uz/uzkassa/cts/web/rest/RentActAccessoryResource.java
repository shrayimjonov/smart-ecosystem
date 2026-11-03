package uz.uzkassa.cts.web.rest;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.cts.dto.rentactaccessory.response.RentActAccessoryResponseDto;
import uz.uzkassa.cts.service.RentActAccessoryService;

import java.util.List;

@RestController
@RequestMapping("/api/rent-act-accessory")
@Tag(name = "Rent act")
@Slf4j
public class RentActAccessoryResource {

    private final RentActAccessoryService service;

    public RentActAccessoryResource(RentActAccessoryService service) {
        this.service = service;
    }

    @Operation(summary ="get active details")
    @GetMapping
    public ResponseEntity<List<RentActAccessoryResponseDto>> getActiveDetails() {
        return ResponseEntity.ok(service.getActiveDetails());
    }

    @Operation(summary ="get active accessories by customerId")
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<RentActAccessoryResponseDto>> getAccessoriesByCustomerId(@PathVariable Long customerId) {
        return ResponseEntity.ok(service.getAccessoriesByCustomerId(customerId));
    }
}
