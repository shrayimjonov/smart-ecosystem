package uz.uzkassa.smartpos.web.rest.terminal;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpos.service.UserService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/terminals/users")
public class TerminalUsersResource {
    private final UserService userService;

    @GetMapping("/roles")
    public List<String> getRolesByLogin(final String login) {
        return userService.getRolesByLogin(login);
    }
}
