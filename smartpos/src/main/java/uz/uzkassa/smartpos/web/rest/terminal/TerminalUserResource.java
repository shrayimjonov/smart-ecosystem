package uz.uzkassa.smartpos.web.rest.terminal;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpos.security.SecurityUtils;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/terminals/users")
public class TerminalUserResource {
    @GetMapping("/current")
    public Long getCurrentUserId() {
        return SecurityUtils.getCurrentUserId();
    }
}
