package uz.uzkassa.horeca.dto.client;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.enums.AgeGroup;
import uz.uzkassa.horeca.enums.Gender;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 23.09.2024
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = PRIVATE)
public class CustomerDetailCabinetDTO extends CommonDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 83954984622L;

    String phone;

    BigDecimal totalSpent;

    Gender gender;

    AgeGroup ageGroup;

    LocalDateTime firstVisitDate;
}
