package uz.uzkassa.smartpossupply.web.rest.errors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Common Supply API error response.")
public class ErrorDTO {

    @Schema(description = "Short error title.", example = "Некорректный запрос")
    String title;

    @Schema(description = "Human-readable error details.", example = "Проверьте параметры запроса")
    String detail;

    @Schema(description = "HTTP status code.", example = "400")
    int status;

    @Schema(description = "Optional legacy application-specific numeric status.", nullable = true)
    Integer customStatus;

    @Schema(description = "Time when the error response was created.", example = "2026-10-07T05:00:00")
    String timestamp;

    @Schema(description = "Request path.", example = "/api/cabinet/v1/reports/preview")
    String path;

    @Schema(description = "Optional related URL.", nullable = true)
    String url;

    @Schema(description = "Whether the client should refresh its access token.", example = "false")
    boolean refreshToken;

    @Schema(description = "Bean Validation errors, when applicable.", nullable = true)
    List<FieldErrorVM> errorFields;

    public ErrorDTO(String title, String detail, int status) {
        this.title = title;
        this.detail = detail;
        this.status = status;
    }
}
