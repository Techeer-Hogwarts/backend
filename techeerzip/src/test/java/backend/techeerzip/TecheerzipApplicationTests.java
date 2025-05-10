package backend.techeerzip;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import backend.techeerzip.infra.s3.S3Service;
import backend.techeerzip.infra.slack.SlackEventHandler;

import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class TecheerzipApplicationTests {
    @MockBean private S3Service s3Service;
    @MockBean private SlackEventHandler slackEventHandler;

    @Test
    void contextLoads() {}
}
