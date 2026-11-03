package uz.uzkassa.smartpos.dto.telegram;

import lombok.*;
import uz.uzkassa.smartpos.domain.postgres.user.TelegramAccount;
import uz.uzkassa.smartpos.dto.base.CommonDTO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A DTO for the {@link TelegramAccount} entity.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TelegramAccountDTO implements Serializable {

    private Long id;

    private String name;

    private String phone;

    private String langKey;

    private Long telegramId;

    private Boolean confirmed;

    private Boolean enabled;

    private Long userId;

    private Long companyId;

    private List<CommonDTO> branches;
    private Long agreementId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TelegramAccountDTO)) {
            return false;
        }

        return id != null && id.equals(((TelegramAccountDTO) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TelegramAccountDTO{" +
               "id=" + getId() +
               ", phone='" + getPhone() + "'" +
               ", name='" + getName() + "'" +
               ", telegramId='" + getTelegramId() + "'" +
               ", confirmed='" + getConfirmed() + "'" +
               ", enabled='" + getEnabled() + "'" +
               ", userId=" + getUserId() +
               ", companyId=" + getCompanyId() +
               "}";
    }

    public void addBranch(CommonDTO branchDTO) {
        branches = branches != null ? branches : new ArrayList<>();
        if (branchDTO != null) {
            getBranches().add(branchDTO);
        }
    }
}
