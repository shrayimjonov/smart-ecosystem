package uz.uzkassa.cts.filter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

/**
 * Created date time: 24.12.2024 15:28
 */

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BenefitRequestFilter extends BaseFilter {

    String priority;

    LocalDate requestDate;

    Long approverId;

    Long typeId;
}
