package uz.uzkassa.cts.dto.caseTypeEmployees;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CaseTypeEmployeesGetDTO implements Serializable {
    String fullName;
    String username;
}
