package backend.techeerzip.infra.index;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.client.RestTemplate;

import backend.techeerzip.global.logger.CustomLogger;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class IndexEventHandler {

    @Value("${INDEX_API_URL}")
    private String indexApiUrl;

    private final RestTemplate restTemplate;
    private final CustomLogger log;

    @Async("defaultExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleCreate(IndexEvent.Create<?> event) {
        final String url = indexApiUrl + "/index/" + event.getIndex();
        try {
            restTemplate.postForEntity(url, event.getPayload(), Void.class);
            log.debug("Index CREATE 요청 보냄: " + url);
        } catch (Exception e) {
            log.error("인덱스 생성 중 오류 발생: " + url + e.getMessage());
        }
    }

    @Async("defaultExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleDelete(IndexEvent.Delete event) {
        final String url =
                String.format(
                        "%s/delete/document?index=%s&id=%s",
                        indexApiUrl, event.getIndex(), event.getId());
        try {
            log.debug("Index DELETE 요청 보냄: " + url);
            restTemplate.delete(url);
        } catch (Exception e) {
            log.error("인덱스 삭제 중 오류 발생: " + url + e.getMessage());
        }
    }
}
