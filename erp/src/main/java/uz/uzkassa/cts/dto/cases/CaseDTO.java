package uz.uzkassa.cts.dto.cases;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.CaseStatus;
import uz.uzkassa.cts.enums.CaseType;
import uz.uzkassa.cts.enums.Priority;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CaseDTO {

    private Long id;

    @NotNull
    private Long customerId;

    private Long assigneeId;

    private Priority priority;

    private CaseStatus status;

    @NotNull
    private CaseType type;
    private Object data;
}
