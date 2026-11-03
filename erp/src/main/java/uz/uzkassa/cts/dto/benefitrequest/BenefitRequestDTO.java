package uz.uzkassa.cts.dto.benefitrequest;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.Priority;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Created date time: 24.12.2024 11:26
 */

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BenefitRequestDTO implements Serializable {

    String description;

    Priority priority;

    LocalDate requestDate;

    Long approverId;

    Long companyId;

    Long typeId;

    BigDecimal quantity;
}
