package uz.uzkassa.smartpos.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
@Setter
public class SignatureAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private String sign;

    public SignatureAuthenticationToken(Object principal, Object credentials, String sign) {
        super(principal, credentials);
        this.sign = sign;
    }

    public SignatureAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities, String sign) {
        super(principal, credentials, authorities);
        this.sign = sign;
    }
}
