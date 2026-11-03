package uz.uzkassa.smartpossupply.web.rest.cabinet.contract;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.async.WebAsyncTask;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.constants.AuthConstants;
import uz.uzkassa.smartpossupply.dto.report.preview.ReportPreviewRequest;
import uz.uzkassa.smartpossupply.dto.report.preview.ReportPreviewResponse;
import uz.uzkassa.smartpossupply.web.rest.errors.ErrorDTO;

@Tag(name = "Report Builder", description = "Report preview execution")
@RequestMapping(ApiConstants.cabinetReportRootApi)
public interface ReportPreviewApi {

    @Operation(
            operationId = "previewReport",
            summary = "Execute a report preview",
            description = "Validates stable report field keys and returns source-calculated report data and totals."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Report preview",
                    content = @Content(schema = @Schema(implementation = ReportPreviewResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid preview specification",
                    content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "401", description = "Authentication is required",
                    content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "403", description = "Report preview is forbidden",
                    content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "422", description = "Preview execution limit exceeded",
                    content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    })
    @Parameter(
            name = AuthConstants.LANGUAGE,
            in = ParameterIn.HEADER,
            description = "Response language. Falls back to the authenticated user's language and then to ru.",
            schema = @Schema(allowableValues = {"ru", "uz", "cyrillic"}, defaultValue = "ru")
    )
    @PostMapping(
            value = ApiConstants.reportPreview,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    WebAsyncTask<ResponseEntity<ReportPreviewResponse>> previewReport(
            @RequestBody ReportPreviewRequest request
    );
}
