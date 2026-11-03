package uz.uzkassa.horeca.security;

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
    private Long branchId;
    private Long departmentId;
    private String language;
    private String tin;

    public CustomUser(
        String username,
        String password,
        Collection<? extends GrantedAuthority> authorities,
        Long userId,
        Long companyId,
        Long branchId,
        Long departmentId,
        String language,
        String tin
    ) {
        super(username, password, authorities);
        this.userId = userId;
        this.companyId = companyId;
        this.branchId = branchId;
        this.departmentId = departmentId;
        this.language = language;
        this.tin = tin;
    }
}
