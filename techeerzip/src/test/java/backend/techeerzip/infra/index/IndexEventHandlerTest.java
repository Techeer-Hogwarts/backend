// package backend.techeerzip.infra.index;
//
// import static org.mockito.ArgumentMatchers.contains;
// import static org.mockito.Mockito.verify;
//
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.test.util.ReflectionTestUtils;
// import org.springframework.web.client.RestTemplate;
//
// import backend.techeerzip.global.logger.CustomLogger;
// import backend.techeerzip.infra.index.IndexEvent.Create;
//
// @ExtendWith(MockitoExtension.class)
// class IndexEventHandlerTest {
//
//    @Mock RestTemplate restTemplate;
//    @Mock private CustomLogger log;
//    @InjectMocks private IndexEventHandler handler;
//
//    @BeforeEach
//    void setUp() {
//        ReflectionTestUtils.setField(handler, "indexApiUrl", "http://localhost");
//    }
//
//    @Test
//    void handleCreateSuccess() {
//        IndexEvent.Create<String> event = new Create<>("project", "payload");
//
//        handler.handleCreate(event);
//
//        verify(restTemplate).postForEntity("http://localhost/index/project", "payload",
// Void.class);
//        verify(log).debug(contains("Index CREATE 요청 보냄: http://localhost/index/project"));
//    }
// }
