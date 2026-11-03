package uz.uzkassa.smartpossupply.web.rest.terminal;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.genericsettings.MobileCompanySettingsListDTO;
import uz.uzkassa.smartpossupply.service.CompanySettingsService;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 07.02.2024
 */
@RestController
@RequestMapping(ApiConstants.terminalCompanySettingsRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class TerminalCompanySettingsResource {

    CompanySettingsService companySettingsService;

    @Operation(summary = ApiConstants.Get, operationId = "getSettingsList")
    @GetMapping
    public ResponseEntity<List<MobileCompanySettingsListDTO>> getList() {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        return ResponseEntity.ok(companySettingsService.getListForMobile());
    }
}