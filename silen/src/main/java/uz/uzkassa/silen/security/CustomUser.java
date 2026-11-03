package uz.uzkassa.silen.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
@Setter
public final class CustomUser extends User {
    private String userId;
    private String branchId;
    private String organizationId;
    private String organizationTin;
    private Long sessionId;

    public CustomUser(String username, String password, Collection<? extends GrantedAuthority> authorities, String userId, String branchId, String organizationId, String organizationTin, Long sessionId) {
        super(username, password, authorities);
        this.userId = userId;
        this.branchId = branchId;
        this.organizationId = organizationId;
        this.organizationTin = organizationTin;
        this.sessionId = sessionId;
    }
}
