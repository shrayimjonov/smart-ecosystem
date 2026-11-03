package uz.uzkassa.horeca.web.rest.cabinet;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.cateringsettings.CateringServiceBatchDTO;
import uz.uzkassa.horeca.dto.cateringsettings.HallFeeListDTO;
import uz.uzkassa.horeca.dto.product.CateringServiceDetailDTO;
import uz.uzkassa.horeca.filters.BaseFilter;
import uz.uzkassa.horeca.service.CateringServiceService;
import uz.uzkassa.horeca.service.HallService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetCateringServiceRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetCateringServiceResource {

    CateringServiceService cateringServiceService;
    HallService hallService;

    @PostMapping
    public ResponseEntity<Long> createBatch(@RequestBody CateringServiceBatchDTO dto) {
        return ResponseEntity.ok(cateringServiceService.createBatch(dto));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<CateringServiceDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(cateringServiceService.getCateringService(id));
    }

    @GetMapping(ApiConstants.hallApi)
    public ResponseEntity<List<HallFeeListDTO>> getHallsFee(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(cateringServiceService.getHallsFee(filter));
    }
}
