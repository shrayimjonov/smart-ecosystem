package uz.uzkassa.cts.dto.smartpos.company;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class CustomerBranchOwnerCtoDTO implements Serializable {

    Long id;

    String firstName;

    String lastName;

    String patronymic;

    String phone;


}
