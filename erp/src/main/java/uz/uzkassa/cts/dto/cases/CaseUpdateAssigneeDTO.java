package uz.uzkassa.cts.dto.cases;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CaseUpdateAssigneeDTO implements Serializable {

    @NotNull
    Long id;

    @NotNull
    Long assigneeId;
}
