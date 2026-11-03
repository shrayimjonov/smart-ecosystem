package uz.uzkassa.smartpossupply.dto.branch;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.address.AddressDTO;
import uz.uzkassa.smartpossupply.enums.BranchType;
import uz.uzkassa.smartpossupply.validator.AdminValidation;
import uz.uzkassa.smartpossupply.validator.CommonValidation;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BranchDTO implements Serializable {

    Long id;

    @NotNull(groups = {CommonValidation.class})
    String name;

    @Valid
    @NotNull(groups = {CommonValidation.class})
    AddressDTO address;

    @NotNull(groups = {AdminValidation.class})
    Long companyId;

    @NotNull(groups = {CommonValidation.class})
    BranchType type;

    Long ownerId;

    @JsonProperty("isMain")
    boolean isMain = false;

    String branchNumber;

    String officialName;

    LocalDateTime registrationDate;
}
