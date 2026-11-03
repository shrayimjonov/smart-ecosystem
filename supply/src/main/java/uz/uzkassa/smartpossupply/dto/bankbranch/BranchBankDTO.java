package uz.uzkassa.smartpossupply.dto.bankbranch;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BranchBankDTO {
    @NotNull(message = "Branch cannot be empty")
    Long branchId;

    @NotNull(message = "Bank cannot be empty")
    Long bankId;

    @Pattern(regexp = "^\\d{20}$", message = "{validation.account.number.length}")
    String accountNumber;

    @NotNull(message = "Oked cannot be blank")
    String oked;
}
