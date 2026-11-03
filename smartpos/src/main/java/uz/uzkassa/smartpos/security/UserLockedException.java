package uz.uzkassa.smartpos.security;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserLockedException extends InternalAuthenticationServiceException {
    public UserLockedException(String msg) {
        super(msg);
    }
}
