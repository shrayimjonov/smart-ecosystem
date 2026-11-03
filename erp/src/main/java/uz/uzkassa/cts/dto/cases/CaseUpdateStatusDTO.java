package uz.uzkassa.cts.dto.cases;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.CaseStatus;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CaseUpdateStatusDTO implements Serializable {

    @NotNull
    Long id;

    @NotNull
    CaseStatus status;
}
