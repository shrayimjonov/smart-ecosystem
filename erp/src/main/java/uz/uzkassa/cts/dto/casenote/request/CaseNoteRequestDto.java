package uz.uzkassa.cts.dto.casenote.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CaseNoteRequestDto {
    @NotNull
    @NotEmpty
    private String message;

    @NotNull
    private Long caseId;
}
