package uz.uzkassa.cts.dto.bonus;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BonusListDTO implements Serializable {

    Long id;

    Long employeeId;

    String employee;

    String branch;

    String company;

    BigDecimal amount;

    BigDecimal bonusAmount;

    Integer count;
}
