package backend.techeerzip.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // .allowedOrigins(
                //     "https://www.techeerzip.cloud", 
                //     "https://api.techeerzip.cloud", 
                //     "https://*.yje.kr",
                //     )
                .allowedOriginPatterns(
                        "http://localhost:*",
                        "http://127.0.0.1:*",
                        "https://*-techeerzip.vercel.app",
                        "https://*.yje.kr",
                        "https://www.techeerzip.cloud",
                        "https://api.techeerzip.cloud")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowCredentials(true);
    }
}
