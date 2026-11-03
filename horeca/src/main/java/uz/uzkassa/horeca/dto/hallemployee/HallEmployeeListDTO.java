package uz.uzkassa.horeca.dto.hallemployee;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.enums.Role;

import java.io.Serializable;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@FieldDefaults(level = PRIVATE)
public class HallEmployeeListDTO implements Serializable {

    Long id;

    CommonDTO hall;

    CommonDTO employee;

    Role role;
}
