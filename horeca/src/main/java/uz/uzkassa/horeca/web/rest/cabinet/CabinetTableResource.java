package uz.uzkassa.horeca.web.rest.cabinet;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.hallitem.HallItemTableDetailDTO;
import uz.uzkassa.horeca.dto.hallitem.HallTableListDTO;
import uz.uzkassa.horeca.service.HallItemService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 25.07.2024
 */
@RestController
@RequestMapping(ApiConstants.cabinetTableRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetTableResource {

    HallItemService hallItemService;

    @GetMapping("/{hallId}")
    public ResponseEntity<List<HallTableListDTO>> getTables(@PathVariable Long hallId) {
        return ResponseEntity.ok(hallItemService.getTables(hallId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HallItemTableDetailDTO> getTable(@PathVariable Long id) {
        return ResponseEntity.ok(hallItemService.getTable(id));
    }
}
