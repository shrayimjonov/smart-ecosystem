package uz.uzkassa.smartpos.web.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpos.security.AuthoritiesConstants;
import uz.uzkassa.smartpos.service.LauncherService;

import java.util.List;
import java.util.Set;

/**
 * REST controller for managing the current user's account.
 */
@Secured(value = {AuthoritiesConstants.ADMIN, AuthoritiesConstants.SMARTPOS_ADMIN})
@RestController
@RequestMapping("/api/launcher")
@RequiredArgsConstructor
public class LauncherResource {
    private final LauncherService launcherService;

    @PostMapping(value = "/block/all")
    public void blockTerminals() {
        launcherService.blockTerminals();
    }

    @PostMapping(value = "/block")
    public void blockTerminals(@RequestBody List<Long> companyIds) {
        launcherService.blockTerminals(companyIds);
    }

    @PostMapping(value = "/unblock")
    public void unblockTerminals(@RequestBody Set<Long> companyIds) {
        launcherService.unblockTerminals(companyIds);
    }

    @PostMapping(value = "/unblock/all")
    public void unblockTerminals() {
        launcherService.unblockTerminals();
    }
}
