package uz.uzkassa.smartpossupply.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.converter.ResolvedSchema;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.BooleanSchema;
import io.swagger.v3.oas.models.media.ComposedSchema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.MapSchema;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.NumberSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.constants.AuthConstants;
import uz.uzkassa.smartpossupply.dto.report.preview.ReportPreviewResponse;
import uz.uzkassa.smartpossupply.dto.report.preview.ReportPreviewResponseFixtures;

import java.util.ArrayList;
import java.util.List;

@Component("reportPreviewResponseOpenApiCustomizer")
public class ReportPreviewResponseOpenApiCustomizer implements OpenApiCustomizer {

    private static final String PATH = ApiConstants.cabinetReportRootApi + ApiConstants.reportPreview;
    private static final String APPLICATION_JSON = org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

    private final ObjectMapper objectMapper;

    public ReportPreviewResponseOpenApiCustomizer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void customise(OpenAPI openApi) {
        if (openApi.getComponents() == null) {
            openApi.setComponents(new Components());
        }

        ResolvedSchema responseSchema = registerSchema(openApi, ReportPreviewResponse.class);
        describeCellValues(responseSchema);
        Operation operation = previewOperation(openApi);

        addLanguageParameter(operation);
        addSuccessResponse(operation, responseSchema.schema);
    }

    private ResolvedSchema registerSchema(OpenAPI openApi, Class<?> type) {
        ResolvedSchema resolved = ModelConverters.getInstance()
                .resolveAsResolvedSchema(new AnnotatedType(type).resolveAsRef(true));
        resolved.referencedSchemas.forEach(openApi.getComponents()::addSchemas);
        return resolved;
    }

    private void describeCellValues(ResolvedSchema resolved) {
        Schema<?> response = resolved.referencedSchemas.get("ReportPreviewResponseV1");
        if (response == null || response.getProperties() == null) {
            return;
        }

        Schema<?> rowsProperty = response.getProperties().get("rows");
        if (!(rowsProperty instanceof ArraySchema rows)
                || !(rows.getItems() instanceof MapSchema row)) {
            return;
        }

        Schema<?> cellValue = new ComposedSchema()
                .addOneOfItem(new StringSchema())
                .addOneOfItem(new NumberSchema())
                .addOneOfItem(new IntegerSchema())
                .addOneOfItem(new BooleanSchema())
                .nullable(true);
        row.setAdditionalProperties(cellValue);
    }

    private Operation previewOperation(OpenAPI openApi) {
        Paths paths = openApi.getPaths();
        if (paths == null) {
            paths = new Paths();
            openApi.setPaths(paths);
        }

        PathItem pathItem = paths.computeIfAbsent(PATH, ignored -> new PathItem());
        Operation operation = pathItem.getPost();
        if (operation == null) {
            operation = new Operation()
                    .operationId("previewReport")
                    .summary("Get a report preview")
                    .description("Returns localized columns and typed JSON values for any report dataset.")
                    .tags(List.of("Report Builder"))
                    .responses(new ApiResponses());
            pathItem.setPost(operation);
        }
        return operation;
    }

    private void addLanguageParameter(Operation operation) {
        if (operation.getParameters() == null) {
            operation.setParameters(new ArrayList<>());
        }
        boolean alreadyPresent = operation.getParameters().stream()
                .anyMatch(parameter -> AuthConstants.LANGUAGE.equals(parameter.getName()));
        if (alreadyPresent) {
            return;
        }

        operation.addParametersItem(new Parameter()
                .name(AuthConstants.LANGUAGE)
                .in("header")
                .required(false)
                .description("Language of column names. Falls back to the authenticated user's language and then to ru.")
                .schema(new Schema<String>()
                        .type("string")
                        ._default("ru")
                        ._enum(List.of("ru", "uz", "cyrillic"))));
    }

    private void addSuccessResponse(Operation operation, Schema<?> schema) {
        if (operation.getResponses() == null) {
            operation.setResponses(new ApiResponses());
        }

        ApiResponse success = operation.getResponses().computeIfAbsent(
                "200",
                ignored -> new ApiResponse()
        );
        success.setDescription("Report preview or an empty successful result");

        MediaType mediaType = new MediaType().schema(schema)
                .addExamples("regionByMonth", new Example()
                        .summary("Sales by region and month")
                        .value(objectMapper.convertValue(ReportPreviewResponseFixtures.regionByMonth(), Object.class)))
                .addExamples("topOutlets", new Example()
                        .summary("Top outlets by sales amount")
                        .value(objectMapper.convertValue(ReportPreviewResponseFixtures.topOutlets(), Object.class)))
                .addExamples("empty", new Example()
                        .summary("No data for the selected period")
                        .value(objectMapper.convertValue(ReportPreviewResponseFixtures.emptyRegionByMonth(), Object.class)));
        success.setContent(new Content().addMediaType(APPLICATION_JSON, mediaType));
    }
}
