package uz.uzkassa.silen.rest;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.silen.domain.User;
import uz.uzkassa.silen.dto.AccountDTO;
import uz.uzkassa.silen.dto.PasswordChangeDTO;
import uz.uzkassa.silen.dto.vm.KeyAndPasswordVM;
import uz.uzkassa.silen.dto.vm.ManagedUserVM;
import uz.uzkassa.silen.exceptions.BadRequestException;
import uz.uzkassa.silen.service.UserService;

import java.util.Optional;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AccountResource {
    private final UserService userService;

    private static boolean checkPasswordLength(String password) {
        return !StringUtils.isEmpty(password) &&
                password.length() >= ManagedUserVM.PASSWORD_MIN_LENGTH &&
                password.length() <= ManagedUserVM.PASSWORD_MAX_LENGTH;
    }

    @GetMapping("/authenticate")
    public String isAuthenticated(HttpServletRequest request) {
        return request.getRemoteUser();
    }
/*

    @PostMapping("/account")
    public void saveAccount(@Valid @RequestBody UserDTO userDTO) {
        String userLogin = Optional.ofNullable(SecurityUtils.getCurrentUserLogin())
            .orElseThrow(() -> new BadRequestException("No user"));
        Optional<User> user = userRepository.findOneByLoginAndDeletedFalse(userLogin);
        if (!user.isPresent()) {
            throw new BadRequestException("No user");
        }
        userService.save(userDTO);
    }
*/

    @GetMapping("/account")
    public AccountDTO getAccount() {
        return userService.getMe();
    }

    @PostMapping(path = "/account/change-password")
    public void changePassword(@RequestBody PasswordChangeDTO passwordChangeDto) {
        if (!checkPasswordLength(passwordChangeDto.getNewPassword())) {
            throw new BadRequestException("Invalid password");
        }
        userService.changePassword(passwordChangeDto.getCurrentPassword(), passwordChangeDto.getNewPassword());
    }

    @PostMapping(path = "/account/reset-password/init")
    public void requestPasswordReset(@RequestBody String mail) {
        userService.requestPasswordReset(mail);
    }

    @PostMapping(path = "/account/reset-password/finish")
    public void finishPasswordReset(@RequestBody KeyAndPasswordVM keyAndPassword) {
        if (!checkPasswordLength(keyAndPassword.getNewPassword())) {
            throw new BadRequestException("Invalid password");
        }
        Optional<User> user =
                userService.completePasswordReset(keyAndPassword.getNewPassword(), keyAndPassword.getKey());

        if (user.isEmpty()) {
            throw new BadRequestException("No user");
        }
    }
}
