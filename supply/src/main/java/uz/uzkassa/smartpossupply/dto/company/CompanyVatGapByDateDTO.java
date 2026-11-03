package uz.uzkassa.smartpossupply.dto.company;

import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.constants.Constants;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompanyVatGapByDateDTO implements Serializable {

    @Pattern(regexp = Constants.TIN_REGEX)
    String tin;

    LocalDate date;
}
