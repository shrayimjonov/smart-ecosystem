package uz.uzkassa.cts.dto.apay;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class DirectorDTO implements Serializable {

    String firstName;

    String lastName;

    String middleName;

    String phone;
}
