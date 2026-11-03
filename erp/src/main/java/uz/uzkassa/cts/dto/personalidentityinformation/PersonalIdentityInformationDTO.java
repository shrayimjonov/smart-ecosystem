package uz.uzkassa.cts.dto.personalidentityinformation;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.DriverLicenseCategory;
import uz.uzkassa.cts.enums.PersonalIdentityType;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class PersonalIdentityInformationDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 39572864827732L;

    Long id;

    String serial;

    Integer number;

    LocalDate issueDate;

    LocalDate expiryDate;

    String issueBy;

    String pinfl;

    List<DriverLicenseCategory> categories;

    PersonalIdentityType type;

    Long employeeId;
}
