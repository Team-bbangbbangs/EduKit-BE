package com.edukit.api.common.config;

import com.edukit.api.common.annotation.MemberId;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {

    private static final String AUTHORIZATION = "Authorization";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(info())
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
