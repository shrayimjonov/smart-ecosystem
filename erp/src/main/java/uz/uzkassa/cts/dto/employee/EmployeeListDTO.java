package uz.uzkassa.cts.dto.employee;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.UserStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class EmployeeListDTO implements Serializable {

    Long id;

    String firstName;

    String lastName;

    String patronymic;

    String position;

    String phone;

    UserStatus status;

    Long branchId;

    String branch;

    LocalDateTime creationDate;
}
