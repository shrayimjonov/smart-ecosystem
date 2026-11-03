package uz.uzkassa.horeca.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.reservation.ReservationDTO;
import uz.uzkassa.horeca.dto.reservation.ReservationDetailDTO;
import uz.uzkassa.horeca.dto.reservation.ReservationListDTO;
import uz.uzkassa.horeca.dto.reservation.ReservationUpdateStatusDTO;
import uz.uzkassa.horeca.enums.CommonStatus;
import uz.uzkassa.horeca.filters.ReservationFilter;
import uz.uzkassa.horeca.service.ReservationService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 16.09.2024
 */
@RestController
@RequestMapping(ApiConstants.adminReservationRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminReservationResource {

    ReservationService reservationService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody ReservationDTO reservationDTO) {
        return ResponseEntity.ok(reservationService.create(reservationDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<ReservationDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.get(id));
    }

    @GetMapping
    public ResponseEntity<Page<ReservationListDTO>> getList(@ParameterObject ReservationFilter filter) {
        return ResponseEntity.ok(reservationService.getList(filter));
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

}
