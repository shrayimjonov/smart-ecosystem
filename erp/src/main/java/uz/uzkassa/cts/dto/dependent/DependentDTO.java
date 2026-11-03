package uz.uzkassa.cts.dto.dependent;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.FullNameDTO;
import uz.uzkassa.cts.enums.Gender;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DependentDTO extends FullNameDTO implements Serializable {

    Gender gender;

    LocalDate birthDate;

    Long employeeId;

    Long relationshipId;
}
