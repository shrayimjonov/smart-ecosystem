package uz.uzkassa.smartpos.dto.elasticsearch;

import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.dto.FullNameDTO;
import uz.uzkassa.smartpos.dto.UserDTO;

import java.io.Serializable;

/**
 * A user.
 */
@Getter
@Setter
public class UserES implements Serializable {
    private Long id;
    private String login;
    private String firstName;
    private String lastName;
    private Long branchId;
    private BranchES branch;
    private Long companyId;
    private CompanyES company;

    @Transient
    public UserDTO getShortDTO() {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(getId());
        userDTO.setFullName(new FullNameDTO(getFirstName(), getLastName(), null));
        return userDTO;
    }

}
