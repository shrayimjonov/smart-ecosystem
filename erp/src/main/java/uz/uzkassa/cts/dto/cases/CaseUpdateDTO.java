package uz.uzkassa.cts.dto.cases;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.CaseType;
import uz.uzkassa.cts.enums.Priority;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CaseUpdateDTO implements Serializable {

    @NotNull
    Long id;

    Priority priority;

    CaseType caseType;

    Long customerId;

    Long assigneeId;
}
