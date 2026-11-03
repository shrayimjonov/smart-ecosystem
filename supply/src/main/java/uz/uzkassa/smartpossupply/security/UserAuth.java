package uz.uzkassa.smartpossupply.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Set;

@Getter
@Setter
public class UserAuth extends User {

    private Long userId;
    private Long companyId;
    private Long branchId;
    private Set<Long> branchIds;
    private Long warehouseId;
    private String language;
    private String tin;

    public UserAuth(
        String username,
        String password,
        Collection<? extends GrantedAuthority> authorities,
        Long userId,
        Long companyId,
        Long branchId,
        Set<Long> branchIds,
        Long warehouseId,
        String language,
        String tin
    ) {
        super(username, password, authorities);
        this.userId = userId;
        this.companyId = companyId;
        this.branchId = branchId;
        this.branchIds = branchIds;
        this.warehouseId = warehouseId;
        this.language = language;
        this.tin = tin;
    }
}
