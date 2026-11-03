package uz.uzkassa.smartpossupply.dto.company;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class BatchCompanyManagerDTO {

    @NotNull
    Long managerId;

    @NotNull
    @NotEmpty
    List<Long> companyIds;
}
