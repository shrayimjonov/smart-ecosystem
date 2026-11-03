package uz.uzkassa.cts.dto.base;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PROTECTED)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FullNameDTO implements Serializable {

    Long id;

    String firstName;

    String lastName;

    String patronymic;
}
