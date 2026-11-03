package uz.uzkassa.smartpossupply.config;

import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.converter.ResolvedSchema;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.report.error.ReportErrorExamples;
import uz.uzkassa.smartpossupply.web.rest.errors.ErrorDTO;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component("reportErrorsOpenApiCustomizer")
public class ReportErrorsOpenApiCustomizer implements OpenApiCustomizer {

    private static final String DATASETS_PATH = ApiConstants.cabinetReportRootApi + ApiConstants.reportDatasets;
    private static final String PREVIEW_PATH = ApiConstants.cabinetReportRootApi + ApiConstants.reportPreview;
    private static final String APPLICATION_JSON = org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

    @Override
    public void customise(OpenAPI openApi) {
        if (openApi.getComponents() == null) {
            openApi.setComponents(new Components());
        }

        ResolvedSchema errorSchema = registerSchema(openApi, ErrorDTO.class);
        addDatasetErrors(operation(openApi, DATASETS_PATH, PathItem.HttpMethod.GET), errorSchema.schema);
        addPreviewErrors(operation(openApi, PREVIEW_PATH, PathItem.HttpMethod.POST), errorSchema.schema);
    }

    private ResolvedSchema registerSchema(OpenAPI openApi, Class<?> type) {
        ResolvedSchema resolved = ModelConverters.getInstance()
                .resolveAsResolvedSchema(new AnnotatedType(type).resolveAsRef(true));
        resolved.referencedSchemas.forEach(openApi.getComponents()::addSchemas);
        return resolved;
    }

    private Operation operation(OpenAPI openApi, String path, PathItem.HttpMethod method) {
        Paths paths = openApi.getPaths();
        if (paths == null) {
            paths = new Paths();
            openApi.setPaths(paths);
        }

        PathItem pathItem = paths.computeIfAbsent(path, ignored -> new PathItem());
        Operation operation = pathItem.readOperationsMap().get(method);
        if (operation != null) {
            return operation;
        }

        operation = new Operation()
                .operationId(method == PathItem.HttpMethod.GET ? "getReportDatasets" : "previewReport")
                .tags(List.of("Report Builder"))
                .responses(new ApiResponses());
        if (method == PathItem.HttpMethod.GET) {
            pathItem.setGet(operation);
        } else {
            pathItem.setPost(operation);
        }
        return operation;
    }

    private void addDatasetErrors(Operation operation, Schema<?> schema) {
        addResponse(operation, "401", "Authentication is required", schema, Map.of(
                "unauthorized", example("Authentication is required", ReportErrorExamples.unauthorizedDatasets())
        ));
        addResponse(operation, "403", "Access to report datasets is forbidden", schema, Map.of(
                "forbidden", example("Access is forbidden", ReportErrorExamples.forbiddenDatasets())
        ));
    }

    private void addPreviewErrors(Operation operation, Schema<?> schema) {
        addResponse(operation, "400", "Invalid preview request", schema, Map.of(
                "validation", example("Bean Validation error", ReportErrorExamples.validation())
        ));
        addResponse(operation, "401", "Authentication is required", schema, Map.of(
                "unauthorized", example("Authentication is required", ReportErrorExamples.unauthorizedPreview())
        ));
        addResponse(operation, "403", "Report preview is forbidden", schema, Map.of(
                "forbidden", example("Access is forbidden", ReportErrorExamples.forbiddenPreview())
        ));

        Map<String, Example> limitExamples = new LinkedHashMap<>();
        limitExamples.put("maxCells", example("Maximum cell count exceeded", ReportErrorExamples.maxCellsExceeded()));
        limitExamples.put("maxRows", example("Maximum row count exceeded", ReportErrorExamples.maxRowsExceeded()));
        limitExamples.put("timeout", example("Statement timeout exceeded", ReportErrorExamples.timeoutExceeded()));
        limitExamples.put("concurrency", example(
                "Concurrent preview limit exceeded",
                ReportErrorExamples.concurrentPreviewsExceeded()
        ));
        addResponse(operation, "422", "Report execution limit exceeded", schema, limitExamples);
    }

    private void addResponse(
            Operation operation,
            String status,
            String description,
            Schema<?> schema,
            Map<String, Example> examples
    ) {
        if (operation.getResponses() == null) {
            operation.setResponses(new ApiResponses());
        }

        MediaType mediaType = new MediaType().schema(schema);
        examples.forEach(mediaType::addExamples);
        operation.getResponses().addApiResponse(status, new ApiResponse()
                .description(description)
                .content(new Content().addMediaType(APPLICATION_JSON, mediaType)));
    }

    private Example example(String summary, Object value) {
        return new Example().summary(summary).value(value);
    }
}
