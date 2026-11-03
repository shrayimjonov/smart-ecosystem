package uz.uzkassa.horeca.web.rest.mobile;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.base.CheckStatusResponseDTO;
import uz.uzkassa.horeca.dto.reservation.ReservationUpdateStatusDTO;
import uz.uzkassa.horeca.dto.reservation.ReservationDTO;
import uz.uzkassa.horeca.dto.reservation.ReservationDetailDTO;
import uz.uzkassa.horeca.dto.reservation.ReservationListDTO;
import uz.uzkassa.horeca.filters.ReservationFilter;
import uz.uzkassa.horeca.service.ReservationService;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 23.02.2024
 */
@RestController
@RequestMapping(ApiConstants.mobileReservationRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MobileReservationResource {

    ReservationService reservationService;

    @Operation(operationId = "reservationCreate")
    @PostMapping
    public ResponseEntity<Long> create(@RequestBody ReservationDTO reservationDTO) {
        return ResponseEntity.ok(reservationService.create(reservationDTO));
    }

    @Operation(operationId = "reservationGetDetail")
    @GetMapping(ApiConstants.id)
    public ResponseEntity<ReservationDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.get(id));
    }

    @Operation(operationId = "reservationUpdate")
    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody ReservationDTO reservationDTO) {
        return ResponseEntity.ok(reservationService.update(id, reservationDTO));
    }

    @Operation(operationId = "reservationGetList")
    @GetMapping
    public ResponseEntity<Page<ReservationListDTO>> getList(@ParameterObject ReservationFilter filter) {
        return ResponseEntity.ok(reservationService.getList(filter));
    }

    @Operation(operationId = "reservationTransfer")
    @PutMapping("/transfer/{id}/{tableId}")
    public ResponseEntity<CheckStatusResponseDTO> transfer(@PathVariable Long id, @PathVariable Long tableId, @RequestParam LocalDateTime lastModifiedDate) {
        return ResponseEntity.ok(reservationService.transfer(id, tableId, lastModifiedDate));
    }

    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestBody ReservationUpdateStatusDTO updateStatusDTO) {
        reservationService.updateStatus(id, updateStatusDTO);
        return ResponseEntity.ok().build();
    }
}
