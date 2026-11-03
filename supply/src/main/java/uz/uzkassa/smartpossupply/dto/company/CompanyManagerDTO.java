package uz.uzkassa.smartpossupply.dto.company;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CompanyManagerDTO {

    @NotNull
    Long companyId;

    @NotNull
    Long managerId;
}
