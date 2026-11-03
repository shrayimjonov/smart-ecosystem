package uz.uzkassa.cts.service;

import uz.uzkassa.cts.domain.User;
import uz.uzkassa.cts.dto.auth.ActivationFinishDTO;
import uz.uzkassa.cts.dto.auth.ChangePasswordDTO;
import uz.uzkassa.cts.dto.auth.ChangeUsernameConfirmDTO;
import uz.uzkassa.cts.dto.user.UserDTO;
import uz.uzkassa.cts.dto.user.admin.CurrentUserAdminDTO;
import uz.uzkassa.cts.dto.user.admin.UserDetailAdminDTO;
import uz.uzkassa.cts.dto.user.cabinet.CurrentUserCabinetDTO;

public interface UserService extends BaseService<User, Long> {

    Long create(UserDTO userDTO);

    Long update(Long id, UserDTO userDTO);

    UserDetailAdminDTO getForAdmin(Long id);

    CurrentUserCabinetDTO getCurrentUserForCabinet();

    CurrentUserAdminDTO getCurrentUserForAdmin();

    void activate(ActivationFinishDTO activationFinishDTO);

    void changePassword(User user, ChangePasswordDTO changePasswordDTO);

    void changeUsername(User user, ChangeUsernameConfirmDTO changeUsernameConfirmDTO);
}
