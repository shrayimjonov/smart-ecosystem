package uz.uzkassa.cts.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import uz.uzkassa.cts.domain.User;
import uz.uzkassa.cts.domain.UserAuth;
import uz.uzkassa.cts.dto.auth.*;
import uz.uzkassa.cts.enums.TemplateType;

public interface UserAuthService extends BaseService<UserAuth, Long> {

    SecretKeyDTO activateStart(ActivationDTO activateDTO);

    void activateFinish(ActivationFinishDTO activationFinishDTO);

    void sendActivationCode(UsernameDTO usernameDTO, TemplateType templateType);

    SecretKeyDTO changePasswordInit(User user);

    SecretKeyDTO changePasswordInit(UsernameDTO usernameDTO);

    SecretKeyDTO changePasswordInit();

    void changePasswordConfirm(User user, ChangePasswordDTO changePasswordDTO);

    void changePasswordConfirm(ChangePasswordPublicDTO changePasswordDTO);

    void changePasswordConfirm(ChangePasswordDTO changePasswordDTO);

    SecretKeyDTO changeUsernameInit(User user, UsernameDTO usernameDTO);

    void changeUsernameConfirm(User user, ChangeUsernameConfirmDTO changeUsernameConfirmDTO, HttpServletRequest request, HttpServletResponse response);
}
