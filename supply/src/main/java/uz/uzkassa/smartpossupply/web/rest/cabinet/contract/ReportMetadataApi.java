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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.constants.AuthConstants;
import uz.uzkassa.smartpossupply.dto.report.metadata.ReportDatasetsResponse;
import uz.uzkassa.smartpossupply.web.rest.errors.ErrorDTO;

@Tag(name = "Report Builder", description = "Report metadata contract")
@RequestMapping(ApiConstants.cabinetReportRootApi)
public interface ReportMetadataApi {

    @Operation(
            operationId = "getReportDatasets",
            summary = "Get supported report datasets",
            description = "Returns stable keys, localized names, fields and public limits."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Dataset metadata",
                    content = @Content(schema = @Schema(implementation = ReportDatasetsResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication is required",
                    content = @Content(schema = @Schema(implementation = ErrorDTO.class))
            )
    })
    @Parameter(
            name = AuthConstants.LANGUAGE,
            in = ParameterIn.HEADER,
            description = "Response language. Falls back to the authenticated user's language and then to ru.",
            schema = @Schema(allowableValues = {"ru", "uz", "cyrillic"}, defaultValue = "ru")
    )
    @GetMapping(value = ApiConstants.reportDatasets, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ReportDatasetsResponse> getReportDatasets();
}
