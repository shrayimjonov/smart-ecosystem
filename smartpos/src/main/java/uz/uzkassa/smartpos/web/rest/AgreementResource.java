package uz.uzkassa.smartpos.web.rest;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.constants.TokenType;
import uz.uzkassa.smartpos.dto.integration.BranchJson;
import uz.uzkassa.smartpos.dto.integration.agreement.AgreementStatus;
import uz.uzkassa.smartpos.dto.integration.agreement.PublicAgreementDTO;
import uz.uzkassa.smartpos.integration.billing.BillingServiceFeignClient;
import uz.uzkassa.smartpos.service.TokenStoreService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/billing-service")
@Slf4j
@RequiredArgsConstructor
public class AgreementResource {
    private final BillingServiceFeignClient billingServiceFeignClient;
    private final TokenStoreService tokenStoreService;

    //    @Secured(SMARTPOS_ADMIN)// we need operator role.
    @GetMapping("/get-actives")
    public ResponseEntity<List<PublicAgreementDTO>> requestForActiveAgreements(@RequestParam(value = "ctoId") Long ctoId, @RequestParam(value = "serviceType") String serviceType, @RequestParam(value = "branchId") Long branchId) {
        return ResponseEntity.ok().body(getActiveAgreements(ctoId, serviceType, branchId));
    }

    private List<PublicAgreementDTO> getActiveAgreements(Long ctoId, String serviceType, Long branchId) {
        ResponseEntity<List<PublicAgreementDTO>> dtos = billingServiceFeignClient.getActiveAgreements(tokenStoreService.getToken(TokenType.BILLING), ctoId, null, serviceType, null);
        List<PublicAgreementDTO> result = new ArrayList<>();
        if (branchId != null) {
            if (dtos.getBody() != null) {
                result.addAll(dtos.getBody());
            }
        } else if (CollectionUtils.isNotEmpty(dtos.getBody())) {
            for (PublicAgreementDTO agreementDTO : dtos.getBody()) {
                if (CollectionUtils.isNotEmpty(agreementDTO.getBranches())) {
                    if (agreementDTO.getBranches().stream().map(BranchJson::getBranchId).toList().contains(null)) {
                        result.add(agreementDTO);
                    }
                }
            }
        }
        return result;
    }

    @GetMapping("/has-active-agreement")
//    @Secured(SMARTPOS_ADMIN)// we need operator role.
    public ResponseEntity<Boolean> checkIfWeHaveActiveAgreement(@RequestParam(value = "ctoId") Long ctoId, @RequestParam(value = "serviceType") String serviceType, @RequestParam(value = "branchId") Long branchId) {
        List<PublicAgreementDTO> result = getActiveAgreements(ctoId, serviceType, branchId);
        return ResponseEntity.ok().body(CollectionUtils.isNotEmpty(result));
    }

    @GetMapping("/has-active-agreement/{ctoId}")
//    @Secured(SMARTPOS_ADMIN)// we need operator role.
    public ResponseEntity<Boolean> checkIfWeHaveActiveAgreement(@PathVariable(value = "ctoId") Long ctoId, @RequestParam(value = "serviceType") String serviceType) {
        List<PublicAgreementDTO> result = getActiveAgreements(ctoId, serviceType, null);
        return ResponseEntity.ok().body(CollectionUtils.isNotEmpty(result));
    }

    @GetMapping("/get-active-ids")
    public ResponseEntity<List<Long>> activeAgreementIds(@RequestParam(value = "serviceType") String serviceType) {
        return billingServiceFeignClient.getActiveAgreementIdsByServiceType(tokenStoreService.getToken(TokenType.BILLING), serviceType);
    }

    /**
     * {@code GET  /services} : Create a new xizmat.
     */
    @GetMapping("/agreement-statuses")
    public ResponseEntity<List<AgreementStatus>> getAgreementStatuses() {
        return ResponseEntity.ok().body(AgreementStatus.asList());
    }
}
