package backend.techeerzip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
// @PropertySource("classpath:env.properties")
public class TecheerzipApplication {

    public static void main(String[] args) {
        SpringApplication.run(TecheerzipApplication.class, args);
    }
}
