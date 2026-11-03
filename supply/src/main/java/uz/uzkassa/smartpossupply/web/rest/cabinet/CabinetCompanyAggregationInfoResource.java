package uz.uzkassa.smartpossupply.web.rest.cabinet;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.aslbelgi.AslBelgiTokenDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyAggregationInfoDTO;
import uz.uzkassa.smartpossupply.service.CompanyAggregationInfoService;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetCompanyAggregationInfoApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetCompanyAggregationInfoResource {

    CompanyAggregationInfoService companyAggregationInfoService;

    @Operation(summary = ApiConstants.Create)
    @PostMapping
    public ResponseEntity<Void> createOrUpdate(@RequestBody CompanyAggregationInfoDTO companyAggregationInfoDTO) {
        companyAggregationInfoService.createOrUpdate(companyAggregationInfoDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = ApiConstants.Get)
    @GetMapping
    public ResponseEntity<CompanyAggregationInfoDTO> getAggregationInfo() {
        return ResponseEntity.ok(companyAggregationInfoService.get(null));
    }

    @Operation(summary = "Refresh token")
    @GetMapping(ApiConstants.refreshToken)
    public ResponseEntity<Void> refreshToken(@RequestParam(value = "tin", required = false) String tin) {
        companyAggregationInfoService.refreshToken(tin);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/asl-belgi-token")
    public ResponseEntity<Void> update(@RequestBody AslBelgiTokenDTO aslBelgiTokenDTO) {
        companyAggregationInfoService.updateToken(aslBelgiTokenDTO);
        return ResponseEntity.ok().build();
    }
}
