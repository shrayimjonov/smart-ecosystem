package uz.uzkassa.cts.filter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

/**
 * Created date time: 25.12.2024 14:43
 */

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EquipmentRequestFilter extends BaseFilter {

    String priority;

    LocalDate requestDate;

    Long approverId;
}
