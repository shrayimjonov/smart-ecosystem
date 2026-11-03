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
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.report.preview.ReportPreviewRequest;
import uz.uzkassa.smartpossupply.dto.report.preview.ReportPreviewRequestFixtures;
import uz.uzkassa.smartpossupply.web.rest.errors.ErrorDTO;

import java.util.List;

@Component("reportPreviewRequestOpenApiCustomizer")
public class ReportPreviewRequestOpenApiCustomizer implements OpenApiCustomizer {

    private static final String PATH = ApiConstants.cabinetReportRootApi + ApiConstants.reportPreview;
    private static final String APPLICATION_JSON = org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

    private final ObjectMapper objectMapper;

    public ReportPreviewRequestOpenApiCustomizer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void customise(OpenAPI openApi) {
        if (openApi.getComponents() == null) {
            openApi.setComponents(new Components());
        }

        ResolvedSchema requestSchema = registerSchema(openApi, ReportPreviewRequest.class);
        ResolvedSchema errorSchema = registerSchema(openApi, ErrorDTO.class);

        Paths paths = openApi.getPaths();
        if (paths == null) {
            paths = new Paths();
            openApi.setPaths(paths);
        }

        PathItem pathItem = paths.computeIfAbsent(PATH, ignored -> new PathItem());
        Operation operation = pathItem.getPost();
        if (operation == null) {
            operation = contractOperation(requestSchema.schema, errorSchema.schema);
            pathItem.setPost(operation);
        }

        addRequestExample(operation, requestSchema.schema);
    }

    private ResolvedSchema registerSchema(OpenAPI openApi, Class<?> type) {
        ResolvedSchema resolved = ModelConverters.getInstance()
                .resolveAsResolvedSchema(new AnnotatedType(type).resolveAsRef(true));
        resolved.referencedSchemas.forEach(openApi.getComponents()::addSchemas);
        return resolved;
    }

    private Operation contractOperation(Schema<?> requestSchema, Schema<?> errorSchema) {
        ApiResponses responses = new ApiResponses()
                .addApiResponse("200", new ApiResponse().description("Report preview with source-calculated totals"))
                .addApiResponse("400", new ApiResponse()
                        .description("Invalid preview specification")
                        .content(content(errorSchema)))
                .addApiResponse("401", new ApiResponse()
                        .description("Authentication is required")
                        .content(content(errorSchema)));

        return new Operation()
                .operationId("previewReport")
                .summary("Validate and execute a report preview specification")
                .description("Accepts stable report field keys and executes a company-scoped, parameterized query.")
                .tags(List.of("Report Builder"))
                .requestBody(new RequestBody()
                        .required(true)
                        .content(content(requestSchema)))
                .responses(responses);
    }

    private void addRequestExample(Operation operation, Schema<?> schema) {
        if (operation.getRequestBody() == null) {
            operation.setRequestBody(new RequestBody().required(true));
        }
        if (operation.getRequestBody().getContent() == null) {
            operation.getRequestBody().setContent(content(schema));
        }

        MediaType mediaType = operation.getRequestBody().getContent()
                .computeIfAbsent(APPLICATION_JSON, ignored -> new MediaType());
        if (mediaType.getSchema() == null) {
            mediaType.setSchema(schema);
        }
        mediaType.addExamples("regionByMonth", new Example()
                .summary("Sales by region and month")
                .value(objectMapper.convertValue(ReportPreviewRequestFixtures.regionByMonth(), Object.class)));
        mediaType.addExamples("topOutlets", new Example()
                .summary("Top outlets by sales amount")
                .value(objectMapper.convertValue(ReportPreviewRequestFixtures.topOutlets(), Object.class)));
    }

    private Content content(Schema<?> schema) {
        return new Content().addMediaType(APPLICATION_JSON, new MediaType().schema(schema));
    }
}
