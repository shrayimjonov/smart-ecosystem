package uz.uzkassa.cts.dto.cases.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CaseAcceptDto {
    @NotNull
    Long caseId;
}
