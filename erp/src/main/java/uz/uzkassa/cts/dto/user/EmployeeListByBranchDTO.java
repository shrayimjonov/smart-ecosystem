package uz.uzkassa.cts.dto.user;

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
public class EmployeeListByBranchDTO implements Serializable {

    Long id;

    String firstName;

    String lastName;

    String patronymic;

    String phone;

    String position;

    String branch;

    Long branchId;
}



