package uz.uzkassa.horeca.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

import java.util.*;

import org.reflections.Reflections;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uz.uzkassa.horeca.constants.ApiConstants;

@Configuration
public class OpenAPIConfiguration {

    @Value("${application.serverUrl}")
    private String serverUrl;

    @Bean
    public OpenAPI openAPI() {
        OpenAPI openAPI = new OpenAPI();
        Components components = new Components();
        openAPI.info(new Info()
                        .title("Horeca API"))
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
    public GroupedOpenApi defaultGroup() {
        return GroupedOpenApi.builder().group("default").pathsToMatch(ApiConstants.api.concat("/**")).build();
    }

    @Bean
    public GroupedOpenApi adminApiGroup() {
        return GroupedOpenApi.builder().group("admin").pathsToMatch(ApiConstants.api.concat(ApiConstants.admin).concat("/**")).build();
    }

    @Bean
    public GroupedOpenApi cabinetApiGroup() {
        return GroupedOpenApi.builder().group("cabinet").pathsToMatch(ApiConstants.api.concat(ApiConstants.cabinet).concat("/**")).build();
    }

    @Bean
    public GroupedOpenApi publicApiGroup() {
        return GroupedOpenApi.builder().group("public").pathsToMatch(ApiConstants.api.concat(ApiConstants.pub).concat("/**")).build();
    }

    @Bean
    public OpenApiCustomizer customGlobalHeaderOpenApiCustomiser() {
        return openApi -> openApi.getComponents().addSchemas("uz.uzkassa.horeca.enums", createCommonStatusSchema());
    }

    private Schema<?> createCommonStatusSchema() {
        ObjectSchema commonStatusSchema = new ObjectSchema();
        commonStatusSchema.addProperty("code", new Schema<String>().type("string"));
        commonStatusSchema.addProperty("name", new Schema<String>().type("string"));
        return commonStatusSchema;
    }


    private Set<Class<? extends Enum>> getEnumClasses() {
        Reflections reflections = new Reflections("uz.uzkassa.horeca.enums");
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
        enumSchema.setProperties(properties);
        return enumSchema;
    }
}
