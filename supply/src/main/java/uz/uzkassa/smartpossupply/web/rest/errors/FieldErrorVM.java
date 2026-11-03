package uz.uzkassa.smartpossupply.web.rest.errors;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Validation error for one request field.")
public class FieldErrorVM {

    @Schema(example = "ReportPreviewRequest")
    private String objectName;

    @Schema(example = "period.from")
    private String field;

    @Schema(example = "Поле обязательно")
    private String message;

    public FieldErrorVM(String objectName, String field, String message) {
        this.objectName = objectName;
        this.field = field;
        this.message = message;
    }
}
