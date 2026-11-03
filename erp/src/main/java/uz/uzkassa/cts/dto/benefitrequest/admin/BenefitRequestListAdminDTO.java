package uz.uzkassa.cts.dto.benefitrequest.admin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.enums.ApproverCommonStatus;
import uz.uzkassa.cts.enums.Priority;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Created date time: 24.12.2024 11:22
 */

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BenefitRequestListAdminDTO implements Serializable {

    Long id;

    String benefitRequestNumber;

    String description;

    ApproverCommonStatus status;

    Priority priority;

    LocalDate requestDate;

    CommonDTO creator;

    CommonDTO approver;

    CommonDTO type;

    BigDecimal quantity;

    LocalDateTime createdDate;
}
