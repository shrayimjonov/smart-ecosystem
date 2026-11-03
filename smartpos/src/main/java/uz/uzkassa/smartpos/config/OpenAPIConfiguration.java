package uz.uzkassa.smartpos.config;

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

import java.util.Collections;
import java.util.List;

@Configuration(proxyBeanMethods = false)
public class OpenAPIConfiguration {
    @Bean
    public OpenAPI openAPI(@Value("${application.serverUrl}") String serverUrl) {
        return new OpenAPI()
                .info(new Info()
                        .title("Smartpos API"))
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
    GroupedOpenApi terminalAPIs() {
        return GroupedOpenApi.builder()
                .group("terminal")
                .pathsToMatch("/api/v1/terminals/**")
                .build();
    }

    @Bean
    GroupedOpenApi defaultAPIs() {
        return GroupedOpenApi.builder()
                .group("default")
                .pathsToMatch("/api/**")
                .build();
    }

}
