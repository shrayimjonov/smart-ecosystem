package uz.uzkassa.smartpossupply.web.rest.cabinet;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.competitorproduct.CompetitorProductDTO;
import uz.uzkassa.smartpossupply.dto.competitorproduct.CompetitorProductListDTO;
import uz.uzkassa.smartpossupply.service.CompetitorProductService;

import java.util.List;

@RestController
@RequestMapping(ApiConstants.cabinetCompetitorRootApi)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetCompetitorProductResource {

    CompetitorProductService competitorProductService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody CompetitorProductDTO competitorProductDTO) {
        return ResponseEntity.ok(competitorProductService.create(competitorProductDTO));
    }

    @GetMapping("/{customerBranchId}")
    public ResponseEntity<List<CompetitorProductListDTO>> getList(@PathVariable Long customerBranchId) {
        return ResponseEntity.ok(competitorProductService.getList(customerBranchId));
    }
}
