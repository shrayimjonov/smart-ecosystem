package uz.uzkassa.smartpossupply.dto.companybank;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CompanyBankDTO implements Serializable {

    Long id;

    Long companyId;

    @NotNull
    Long bankId;

    @NotNull
    String accountNumber;

    @NotNull
    String oked;

    Boolean main;
}
