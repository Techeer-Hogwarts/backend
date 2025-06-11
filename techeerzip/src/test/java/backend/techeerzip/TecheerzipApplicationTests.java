package backend.techeerzip;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import backend.techeerzip.infra.googleDrive.service.GoogleDriveService;
import backend.techeerzip.infra.s3.S3Service;
import backend.techeerzip.infra.slack.SlackEventHandler;

@SpringBootTest
@ActiveProfiles("test")
class TecheerzipApplicationTests {
    @MockBean private S3Service s3Service;
    @MockBean private SlackEventHandler slackEventHandler;
    @MockBean private GoogleDriveService googleDriveService;

    @Test
    void contextLoads() {}
}
