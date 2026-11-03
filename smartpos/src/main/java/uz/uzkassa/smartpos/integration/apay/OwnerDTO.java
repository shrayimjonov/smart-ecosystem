package uz.uzkassa.smartpos.integration.apay;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import uz.uzkassa.smartpos.integration.enums.GenderEnum;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class OwnerDTO implements Serializable {

    String id;

    String firstName;

    String lastName;

    String middleName;

    GenderEnum gender;

    LocalDate dateOfBirth;

    String phone;

    String login;
}
