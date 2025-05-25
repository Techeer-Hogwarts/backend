package backend.techeerzip.global.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

    @Value("${https.server.url}")
    private String httpsServerUrl;

    @Value("${staging.server.url}")
    private String stagingServerUrl;

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info().title("TecheerZip API Docs").version("1.0").description("API 명세서");

        // Security 스키마 설정 - 쿠키 기반 인증
        SecurityScheme securityScheme =
                new SecurityScheme()
                        .type(SecurityScheme.Type.APIKEY)
                        .in(SecurityScheme.In.COOKIE)
                        .name("access_token")
                        .description("JWT token in cookie");

        // Security 요청 설정
        SecurityRequirement securityRequirement = new SecurityRequirement().addList("cookieAuth");

        // 서버 URL 설정
        Server httpsServer = new Server();
        httpsServer.setUrl(httpsServerUrl);
        httpsServer.setDescription("techeerzip https 서버입니다.");

        Server stagingServer = new Server();
        stagingServer.setUrl(stagingServerUrl);
        stagingServer.setDescription("techeerzip 스테이징 서버입니다.");

        Server localServer = new Server();
        localServer.setUrl("http://localhost:8000");
        localServer.setDescription("techeerzip 로컬 서버입니다.");

        return new OpenAPI()
                .components(new Components().addSecuritySchemes("cookieAuth", securityScheme))
                .addSecurityItem(securityRequirement)
                .info(info)
                .servers(List.of(httpsServer, stagingServer, localServer));
    }
}
