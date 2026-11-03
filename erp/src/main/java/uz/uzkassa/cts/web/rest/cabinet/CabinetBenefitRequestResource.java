package uz.uzkassa.cts.web.rest.cabinet;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.benefitrequest.BenefitRequestDTO;
import uz.uzkassa.cts.dto.benefitrequest.cabinet.BenefitRequestDetailCabinetDTO;
import uz.uzkassa.cts.dto.benefitrequest.cabinet.BenefitRequestListCabinetDTO;
import uz.uzkassa.cts.enums.ApproverCommonStatus;
import uz.uzkassa.cts.filter.BenefitRequestFilter;
import uz.uzkassa.cts.service.BenefitRequestService;

import static lombok.AccessLevel.PRIVATE;

/**
 * Created date time: 23.12.2024 17:51
 */

@RestController
@RequestMapping(ApiConstants.cabinetBenefitRequestRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetBenefitRequestResource {

    BenefitRequestService benefitRequestService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody BenefitRequestDTO benefitRequestDTO) {
        return ResponseEntity.ok(benefitRequestService.create(benefitRequestDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody BenefitRequestDTO benefitRequestDTO) {
        return ResponseEntity.ok(benefitRequestService.update(id, benefitRequestDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<BenefitRequestDetailCabinetDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(benefitRequestService.getForCabinet(id));
    }

    @GetMapping
    public ResponseEntity<Page<BenefitRequestListCabinetDTO>> getList(@ParameterObject BenefitRequestFilter filter) {
        return ResponseEntity.ok(benefitRequestService.getListForCabinet(filter));
    }

    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam ApproverCommonStatus status) {
        benefitRequestService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }
}
