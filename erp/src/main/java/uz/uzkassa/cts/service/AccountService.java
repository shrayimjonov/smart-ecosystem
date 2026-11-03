package uz.uzkassa.cts.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import uz.uzkassa.cts.domain.User;
import uz.uzkassa.cts.dto.auth.LoginRememberMeDTO;

public interface AccountService extends BaseService<User, Long> {

    String getToken(LoginRememberMeDTO loginRememberMeDTO);

    void logout(HttpServletRequest request, HttpServletResponse response);
}
