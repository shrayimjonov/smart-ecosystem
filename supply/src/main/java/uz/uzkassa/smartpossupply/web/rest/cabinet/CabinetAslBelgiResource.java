package uz.uzkassa.smartpossupply.web.rest.cabinet;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.aslbelgi.AuthKey;
import uz.uzkassa.smartpossupply.dto.eimzo.Pkcs7Dto;
import uz.uzkassa.smartpossupply.integration.AslBelgiClient;
import uz.uzkassa.smartpossupply.integration.EimzoClient;
import uz.uzkassa.smartpossupply.service.CompanyAggregationInfoService;
import uz.uzkassa.smartpossupply.web.rest.errors.BadRequestException;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@RestController
@RequestMapping(ApiConstants.cabinetAslBelgiRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetAslBelgiResource {

    AslBelgiClient aslBelgiClient;
    CompanyAggregationInfoService companyAggregationInfoService;
    EimzoClient eimzoClient;

    @GetMapping("/auth-key")
    public ResponseEntity<AuthKey> getGuid() {
        return ResponseEntity.ok(aslBelgiClient.getAuthKey());
    }

    @PostMapping("/sign-in")
    public ResponseEntity<String> signIn(@RequestBody @Valid AuthKey key, @RequestParam(value = "companyId", required = false) Long companyId) {
        Pkcs7Dto pkcs7Dto = eimzoClient.frontTimestamp(key.getData());
        if (pkcs7Dto.isSuccess()) {
            key.setData(pkcs7Dto.getPkcs7b64());
            String token = aslBelgiClient.signIn(key);
            companyAggregationInfoService.updateAslBelgiToken(token, companyId);
            return ResponseEntity.ok(token);
        } else {
            throw new BadRequestException("Not verified, reason: " + pkcs7Dto.getMessage());
        }
    }
}
