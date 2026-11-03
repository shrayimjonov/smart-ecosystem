package uz.uzkassa.smartpos.domain.postgres.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.domain.postgres.base.SimpleEntity;
import uz.uzkassa.smartpos.domain.postgres.company.Branch;
import uz.uzkassa.smartpos.domain.postgres.company.Company;
import uz.uzkassa.smartpos.dto.UserDTO;
import uz.uzkassa.smartpos.enums.EntityType;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A TelegramAccount.
 */
@Entity
@Table(name = "telegram_account")
@Getter
@Setter
public class TelegramAccount extends SimpleEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "phone")
    private String phone;

    @Column(name = "name")
    private String name;

    @Column(name = "telegram_id")
    private Long telegramId;

    @Column(name = "confirmed")
    private Boolean confirmed;

    @Column(name = "enabled")
    private Boolean enabled;

    @Column(name = "agreement_id")
    private Long agreementId;

    @Column(name = "agreement_name")
    private String agreementName;

    @Column(name = "user_id")
    private Long userId;
    @ManyToOne(cascade = {CascadeType.PERSIST}, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", updatable = false, insertable = false)
    @JsonIgnore
    private User user;

    @Column(name = "company_id")
    private Long companyId;
    @ManyToOne(cascade = {CascadeType.PERSIST}, fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    @JsonIgnore
    private Company company;

    @ManyToMany
    @JoinTable(
        name = "telegram_branches",
        joinColumns = {@JoinColumn(name = "telegram_id", referencedColumnName = "id")},
        inverseJoinColumns = {@JoinColumn(name = "branch_id", referencedColumnName = "id")})
    private Set<Branch> branches = new HashSet<>();

    @Setter
    @Getter
    private String langKey;

    public Set<Branch> getBranches() {
        return branches == null ? new HashSet<>() : branches;
    }

    @Override
    public EntityType getHistoryType() {
        return EntityType.TELEGRAM_ACCOUNT;
    }

    public Boolean isConfirmed() {
        return confirmed != null && confirmed;
    }

    public Boolean isEnabled() {
        return enabled != null && enabled;
    }

    public Boolean isUzbek() {
        return getLangKey() != null && !"ru".equalsIgnoreCase(getLangKey());
    }

    public UserDTO getTelegramDTO() {
        UserDTO dto = new UserDTO();
        dto.setId(getId());
        dto.setLogin(getPhone());
        if (getUser() != null) {
            dto.setFullName(getUser().getFullName());
            dto.setBranchId(getUser().getBranchId());
            if (getUser().getBranch() != null) {
                dto.setBranchName(getUser().getBranch().getName());
            }
        }
        if (getCompany() != null) {
            dto.setCompanyId(getCompanyId());
            dto.setCompanyName(getCompany().getName());
            dto.setCompanyInn(getCompany().getInn());
        }
        dto.setTelegramLogin(getPhone());
        dto.setTelegramName(getName());
        return dto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TelegramAccount that = (TelegramAccount) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getId());
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TelegramAccount{" +
            "id=" + getId() +
            ", phone='" + getPhone() + "'" +
            ", name='" + getName() + "'" +
            ", telegramId='" + getTelegramId() + "'" +
            ", confirmed='" + isConfirmed() + "'" +
            ", enabled='" + isEnabled() + "'" +
            "}";
    }

}
