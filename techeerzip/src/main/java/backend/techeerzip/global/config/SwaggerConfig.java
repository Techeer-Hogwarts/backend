package backend.techeerzip.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("TecheerZip API Docs")
                .version("1.0")
                .description("API 명세서");

        // Security 스키마 설정 - 쿠키 기반 인증
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.COOKIE)
                .name("access_token")
                .description("JWT token in cookie");

        // Security 요청 설정
        SecurityRequirement securityRequirement = new SecurityRequirement().addList("cookieAuth");

        return new OpenAPI()
                .components(new Components().addSecuritySchemes("cookieAuth", securityScheme))
                .addSecurityItem(securityRequirement)
                .info(info);
    }
} 