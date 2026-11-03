package uz.uzkassa.cts.dto.caseTypeAccess;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CaseTypeAccessDTO implements Serializable {

    Long id;

    @NotNull
    Long employeeId;

    @NotNull
    List<String> caseTypes;
}
