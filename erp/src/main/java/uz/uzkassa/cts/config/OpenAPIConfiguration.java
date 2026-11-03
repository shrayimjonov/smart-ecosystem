package uz.uzkassa.cts.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uz.uzkassa.cts.constants.ApiConstants;

import java.util.Collections;
import java.util.List;

@Configuration
public class OpenAPIConfiguration {

    @Value("${application.serverUrl}")
    private String serverUrl;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Cts API"))
            .servers(List.of(new Server().url(serverUrl)))
            .components(new Components()
                .addSecuritySchemes("access_token", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .bearerFormat("JWT")
                    .scheme("bearer"))
            )
            .security(Collections.singletonList(new SecurityRequirement().addList("access_token")));
    }

    @Bean
    public GroupedOpenApi allGroup() {
        return GroupedOpenApi.builder().group("all").pathsToMatch(ApiConstants.api.concat("/**")).build();
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
    public GroupedOpenApi mobileApiGroup() {
        return GroupedOpenApi.builder().group("mobile").pathsToMatch(ApiConstants.api.concat(ApiConstants.mobile).concat("/**")).build();
    }
}
