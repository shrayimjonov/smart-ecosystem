package uz.uzkassa.smartpossupply.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.enums.Vat;

import java.util.*;

@Configuration(proxyBeanMethods = false)
@Slf4j
public class OpenAPIConfiguration {

    @Value("${application.serverUrl}")
    private String serverUrl;

    @Bean
    public OpenAPI openAPI() {
        OpenAPI openAPI = new OpenAPI();
        Components components = new Components();
        openAPI.info(new Info()
                        .title("Supply API"))
                .servers(List.of(new Server().url(serverUrl)))
                .components(components
                        .addSecuritySchemes("access_token", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .bearerFormat("JWT")
                                .scheme("bearer"))
                )
                .security(Collections.singletonList(new SecurityRequirement().addList("access_token")));
        Set<Class<? extends Enum>> enumClasses = getEnumClasses();
        for (Class<? extends Enum> enumClass : enumClasses) {
            components.addSchemas(enumClass.getSimpleName(), createEnumSchema(enumClass));
        }
        return openAPI;
    }

    @Bean
    public GroupedOpenApi mobileApiGroup() {
        return GroupedOpenApi.builder().group("mobile").pathsToMatch(ApiConstants.api.concat(ApiConstants.mobile).concat("/**")).build();
    }

    @Bean
    public GroupedOpenApi defaultGroup(
            @Qualifier("reportMetadataOpenApiCustomizer") OpenApiCustomizer reportMetadataOpenApiCustomizer,
            @Qualifier("reportPreviewRequestOpenApiCustomizer") OpenApiCustomizer reportPreviewRequestOpenApiCustomizer,
            @Qualifier("reportPreviewResponseOpenApiCustomizer") OpenApiCustomizer reportPreviewResponseOpenApiCustomizer,
            @Qualifier("reportErrorsOpenApiCustomizer") OpenApiCustomizer reportErrorsOpenApiCustomizer
    ) {
        return GroupedOpenApi.builder()
                .group("default")
                .pathsToMatch(ApiConstants.api.concat("/**"))
                .addOpenApiCustomizer(reportMetadataOpenApiCustomizer)
                .addOpenApiCustomizer(reportPreviewRequestOpenApiCustomizer)
                .addOpenApiCustomizer(reportErrorsOpenApiCustomizer)
                .addOpenApiCustomizer(reportPreviewResponseOpenApiCustomizer)
                .build();
    }

    @Bean
    public GroupedOpenApi adminApiGroup() {
        return GroupedOpenApi.builder().group("admin").pathsToMatch(ApiConstants.api.concat(ApiConstants.admin).concat("/**")).build();
    }

    @Bean
    public GroupedOpenApi cabinetApiGroup(
            @Qualifier("reportMetadataOpenApiCustomizer") OpenApiCustomizer reportMetadataOpenApiCustomizer,
            @Qualifier("reportPreviewRequestOpenApiCustomizer") OpenApiCustomizer reportPreviewRequestOpenApiCustomizer,
            @Qualifier("reportErrorsOpenApiCustomizer") OpenApiCustomizer reportErrorsOpenApiCustomizer,
            @Qualifier("reportPreviewResponseOpenApiCustomizer") OpenApiCustomizer reportPreviewResponseOpenApiCustomizer
    ) {
        return GroupedOpenApi.builder()
                .group("cabinet")
                .pathsToMatch(ApiConstants.api.concat(ApiConstants.cabinet).concat("/**"))
                .addOpenApiCustomizer(reportMetadataOpenApiCustomizer)
                .addOpenApiCustomizer(reportPreviewRequestOpenApiCustomizer)
                .addOpenApiCustomizer(reportErrorsOpenApiCustomizer)
                .addOpenApiCustomizer(reportPreviewResponseOpenApiCustomizer)
                .build();
    }

    @Bean
    public GroupedOpenApi terminalApiGroup() {
        return GroupedOpenApi.builder().group("terminal").pathsToMatch(ApiConstants.api.concat(ApiConstants.terminal).concat("/**")).build();
    }

    @Bean
    public GroupedOpenApi hippoApiGroup() {
        return GroupedOpenApi.builder().group("hippo").pathsToMatch(ApiConstants.api.concat(ApiConstants.hippo).concat("/**")).build();
    }

    @Bean
    public GroupedOpenApi publicApiGroup() {
        return GroupedOpenApi.builder().group("public").pathsToMatch(ApiConstants.api.concat(ApiConstants.publicStr).concat("/**")).build();
    }

    @Bean
    public GroupedOpenApi telegramApiGroup() {
        return GroupedOpenApi.builder().group("telegram").pathsToMatch(ApiConstants.api.concat(ApiConstants.telegram).concat("/**")).build();
    }

    @Bean
    public GroupedOpenApi marketplaceApiGroup() {
        return GroupedOpenApi.builder().group("marketplace").pathsToMatch(ApiConstants.api.concat(ApiConstants.marketplace).concat("/**")).build();
    }

    @Bean
    public OpenApiCustomizer customGlobalHeaderOpenApiCustomiser() {
        return openApi -> openApi.getComponents().addSchemas("uz.uzkassa.smartpossupply.enums", createCommonStatusSchema());
    }

    private Schema<?> createCommonStatusSchema() {
        ObjectSchema commonStatusSchema = new ObjectSchema();
        commonStatusSchema.addProperty("code", new Schema<String>().type("string"));
        commonStatusSchema.addProperty("name", new Schema<String>().type("string"));
        return commonStatusSchema;
    }


    private Set<Class<? extends Enum>> getEnumClasses() {
        Reflections reflections = new Reflections("uz.uzkassa.smartpossupply.enums");
        return reflections.getSubTypesOf(Enum.class);
    }

    private <T extends Enum<T>> Schema<?> createEnumSchema(Class<T> enumClass) {
        ObjectSchema enumSchema = new ObjectSchema();
        enumSchema.setTitle(enumClass.getSimpleName());
        enumSchema.setType("object");
        enumSchema.setName(enumClass.getName());
        Map<String, Schema> properties = new HashMap<>();
        properties.put("code", new Schema<String>().type("string"));
        properties.put("name", new Schema<String>().type("string"));

        /* Mobile team generates client models from this swagger spec; Vat needs "amount"
            exposed so VAT calculation stays dynamic on their side (runtime JSON already
            returns it via @JsonFormat(Shape.OBJECT), only the spec was missing it). */
        /* Mobile jamoasi shu swagger spetsifikatsiyasidan client modellarini generatsiya qiladi;
            ular tomonida QQS hisob-kitobi dinamik bo'lishi uchun Vat'da "amount" ko'rsatilishi
            kerak — runtime JSON uni @JsonFormat(Shape.OBJECT) orqali allaqachon qaytaradi, faqat spetsifikatsiyada yo'q edi. */
        if (enumClass == Vat.class) {
            properties.put("amount", new Schema<Number>().type("number"));
        }

        enumSchema.setProperties(properties);
        return enumSchema;
    }
}
