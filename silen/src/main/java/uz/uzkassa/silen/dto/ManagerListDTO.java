package uz.uzkassa.silen.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ManagerListDTO extends SubscribeDTO {

    String id;

    String organizationName;

    String tin;

    String lastName;

    String firstName;
}
