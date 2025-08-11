package com.edukit.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {

    @Value("${springdoc.server-url}")
    private String serverUrl;
    private static final String AUTHORIZATION = "Authorization";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(info())
                .addServersItem(server())
                .addSecurityItem(securityRequirement())
                .components(components());
    }

    private Info info() {
        Info info = new Info();
        info.title("Edukit API");
        info.description("Edukit API 명세서");
        return info;
    }

    private Server server() {
        return new Server().url(serverUrl);
    }

    private SecurityRequirement securityRequirement() {
        return new SecurityRequirement().addList(AUTHORIZATION);
    }

    private Components components() {
        SecurityScheme apiAuth = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .name(AUTHORIZATION)
                .in(SecurityScheme.In.HEADER)
                .scheme("Bearer")
                .bearerFormat("JWT");
        return new Components().addSecuritySchemes(AUTHORIZATION, apiAuth);
    }
}
