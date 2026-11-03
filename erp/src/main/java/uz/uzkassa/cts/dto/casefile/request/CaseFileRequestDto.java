package uz.uzkassa.cts.dto.casefile.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.enums.DocumentType;

@Getter
@Setter
public class CaseFileRequestDto {
    @NotNull
    private Long caseId;
    @NotNull
    private DocumentType documentType;
    private String description;

}
