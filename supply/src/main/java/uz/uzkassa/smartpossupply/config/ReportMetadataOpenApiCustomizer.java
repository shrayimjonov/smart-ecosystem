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
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.constants.AuthConstants;
import uz.uzkassa.smartpossupply.dto.report.metadata.ReportDatasetsResponse;
import uz.uzkassa.smartpossupply.dto.report.metadata.ReportMetadataFixtures;
import uz.uzkassa.smartpossupply.web.rest.errors.ErrorDTO;

import java.util.List;

@Component("reportMetadataOpenApiCustomizer")
public class ReportMetadataOpenApiCustomizer implements OpenApiCustomizer {

    private static final String PATH = ApiConstants.cabinetReportRootApi + ApiConstants.reportDatasets;
    private static final String APPLICATION_JSON = org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

    private final ObjectMapper objectMapper;

    public ReportMetadataOpenApiCustomizer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void customise(OpenAPI openApi) {
        if (openApi.getComponents() == null) {
            openApi.setComponents(new Components());
        }

        ResolvedSchema metadataSchema = registerSchema(openApi, ReportDatasetsResponse.class);
        ResolvedSchema errorSchema = registerSchema(openApi, ErrorDTO.class);

        Paths paths = openApi.getPaths();
        if (paths == null) {
            paths = new Paths();
            openApi.setPaths(paths);
        }

        PathItem pathItem = paths.computeIfAbsent(PATH, ignored -> new PathItem());
        Operation operation = pathItem.getGet();
        if (operation == null) {
            operation = contractOperation(metadataSchema.schema, errorSchema.schema);
            pathItem.setGet(operation);
        }

        addSuccessExample(operation, metadataSchema.schema);
    }

    private ResolvedSchema registerSchema(OpenAPI openApi, Class<?> type) {
        ResolvedSchema resolved = ModelConverters.getInstance()
                .resolveAsResolvedSchema(new AnnotatedType(type).resolveAsRef(true));
        resolved.referencedSchemas.forEach(openApi.getComponents()::addSchemas);
        return resolved;
    }

    private Operation contractOperation(Schema<?> metadataSchema, Schema<?> errorSchema) {
        ApiResponses responses = new ApiResponses()
                .addApiResponse("200", new ApiResponse()
                        .description("Dataset metadata")
                        .content(content(metadataSchema)))
                .addApiResponse("401", new ApiResponse()
                        .description("Authentication is required")
                        .content(content(errorSchema)));

        return new Operation()
                .operationId("getReportDatasets")
                .summary("Get supported report datasets")
                .description("Returns stable keys, localized names, fields and public limits.")
                .tags(List.of("Report Builder"))
                .addParametersItem(languageParameter())
                .responses(responses);
    }

    private Parameter languageParameter() {
        return new Parameter()
                .name(AuthConstants.LANGUAGE)
                .in("header")
                .required(false)
                .description("Response language. Falls back to the authenticated user's language and then to ru.")
                .schema(new Schema<String>()
                        .type("string")
                        ._default("ru")
                        ._enum(List.of("ru", "uz", "cyrillic")));
    }

    private void addSuccessExample(Operation operation, Schema<?> schema) {
        if (operation.getResponses() == null) {
            operation.setResponses(new ApiResponses());
        }

        ApiResponse success = operation.getResponses().computeIfAbsent(
                "200",
                ignored -> new ApiResponse().description("Dataset metadata")
        );
        if (success.getContent() == null) {
            success.setContent(content(schema));
        }

        MediaType mediaType = success.getContent().computeIfAbsent(APPLICATION_JSON, ignored -> new MediaType());
        if (mediaType.getSchema() == null) {
            mediaType.setSchema(schema);
        }
        mediaType.setExample(objectMapper.convertValue(ReportMetadataFixtures.salesResponse(), Object.class));
    }

    private Content content(Schema<?> schema) {
        return new Content().addMediaType(APPLICATION_JSON, new MediaType().schema(schema));
    }
}
