package uz.uzkassa.smartpos.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
@Setter
public class CustomUser extends User {
    private Long userId;
    private Long companyId;
    private String language;
    private Long branchId;
    private Long branchGroupId;

    public CustomUser(String username, String password, Collection<? extends GrantedAuthority> authorities,
                      Long userId, Long companyId, Long branchId, Long branchGroupId) {
        super(username, password, authorities);
        this.userId = userId;
        this.companyId = companyId;
        this.branchId = branchId;
        this.branchGroupId = branchGroupId;
    }

    public CustomUser(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }
}
