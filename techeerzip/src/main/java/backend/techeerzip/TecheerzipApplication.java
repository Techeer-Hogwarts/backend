package backend.techeerzip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:env.properties")
public class TecheerzipApplication {

    public static void main(String[] args) {
        SpringApplication.run(TecheerzipApplication.class, args);
    }
}
