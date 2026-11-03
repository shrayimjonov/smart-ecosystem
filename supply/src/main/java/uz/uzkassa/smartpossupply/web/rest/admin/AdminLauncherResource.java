package uz.uzkassa.smartpossupply.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.launcher.LauncherActionDTO;
import uz.uzkassa.smartpossupply.service.LauncherService;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 11.12.2024
 */

@RestController
@RequestMapping(ApiConstants.adminLauncherRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminLauncherResource {

    LauncherService launcherService;

    @PostMapping(value = "/block/all/{companyId}")
    public ResponseEntity<Void> blockTerminals(@PathVariable Long companyId) {
        launcherService.blockTerminalsByCompanyId(companyId);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/block")
    public ResponseEntity<Void> blockTerminals(@RequestBody LauncherActionDTO launcherActionDTO) {
        launcherService.blockTerminals(launcherActionDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/unblock")
    public ResponseEntity<Void> unblockTerminals(@RequestBody LauncherActionDTO launcherActionDTO) {
        launcherService.unBlockTerminals(launcherActionDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/unblock/all/{companyId}")
    public ResponseEntity<Void> unblockTerminals(@PathVariable Long companyId) {
        launcherService.unblockTerminalsByCompanyId(companyId);
        return ResponseEntity.ok().build();
    }
}
