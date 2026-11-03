package uz.uzkassa.horeca.web.rest.cabinet;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.base.CheckStatusResponseDTO;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.reservation.ReservationDTO;
import uz.uzkassa.horeca.dto.reservation.ReservationDetailDTO;
import uz.uzkassa.horeca.dto.reservation.ReservationListDTO;
import uz.uzkassa.horeca.dto.reservation.ReservationUpdateStatusDTO;
import uz.uzkassa.horeca.enums.CommonStatus;
import uz.uzkassa.horeca.enums.ReservationStatus;
import uz.uzkassa.horeca.filters.ReservationFilter;
import uz.uzkassa.horeca.interfaces.CrudRestAdvanced;
import uz.uzkassa.horeca.interfaces.StatusUpdater;
import uz.uzkassa.horeca.service.ReservationService;

import java.time.LocalDateTime;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetReservationRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetReservationResource {

    ReservationService reservationService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody ReservationDTO reservationDTO) {
        return ResponseEntity.ok(reservationService.create(reservationDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<ReservationDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.get(id));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody ReservationDTO reservationDTO) {
        return ResponseEntity.ok(reservationService.update(id, reservationDTO));
    }

    @GetMapping
    public ResponseEntity<Page<ReservationListDTO>> getList(@ParameterObject ReservationFilter filter) {
        return ResponseEntity.ok(reservationService.getList(filter));
    }

    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id, @RequestParam LocalDateTime lastModifiedDate) {
        reservationService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<CommonDTO>> lookUp(@ParameterObject ReservationFilter filter) {
        filter.setStatus(CommonStatus.ACTIVE.getCode());
        return ResponseEntity.ok(reservationService.lookUp(filter));
    }

    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestBody ReservationUpdateStatusDTO updateStatusDTO) {
        reservationService.updateStatus(id, updateStatusDTO);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/transfer/{id}/{tableId}")
    public ResponseEntity<CheckStatusResponseDTO> transfer(@PathVariable Long id, @PathVariable Long tableId, @RequestParam LocalDateTime lastModifiedDate) {
        return ResponseEntity.ok(reservationService.transfer(id, tableId, lastModifiedDate));
    }
}
