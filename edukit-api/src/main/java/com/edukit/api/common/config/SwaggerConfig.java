package com.edukit.api.common.config;

import com.edukit.api.common.annotation.MemberId;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {

    private final Environment environment;

    private static final String AUTHORIZATION = "Authorization";
    private static final Map<String, String> PROFILE_SERVER_URL_MAP = Map.of(
            "local", "http://localhost:8080",
            "dev", "https://dev-api.edukit.co.kr",
            "prod", "https://prod-api.edukit.co.kr"
    );

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(info())
                .servers(servers())
                .addSecurityItem(securityRequirement())
                .components(components());
    }

    @Bean
    public OperationCustomizer customizeOperation() {
        return (operation, handlerMethod) -> {
            boolean hasMemberId = Arrays.stream(handlerMethod.getMethodParameters())
                    .anyMatch(param -> param.hasParameterAnnotation(MemberId.class));
            if (hasMemberId) {
                operation.getParameters().removeIf(param -> "memberId".equals(param.getName()));
            }
            return operation;
        };
    }

    private Info info() {
        Info info = new Info();
        info.title("Edukit API");
        info.description("Edukit API 명세서");
        return info;
    }

    private List<Server> servers() {
        return PROFILE_SERVER_URL_MAP.entrySet().stream()
                .filter(entry -> environment.matchesProfiles(entry.getKey()))
                .map(entry -> {
                    String url = entry.getValue();
                    String description = "Edukit API " + entry.getKey();
                    return new Server().url(url).description(description);
                })
                .toList();
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
