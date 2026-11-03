package uz.uzkassa.horeca.dto.hallemployee;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@FieldDefaults(level = PRIVATE)

public class HallEmployeeBatchDTO implements Serializable {

    Long hallId;

    List<Long> employeeIds;
}
