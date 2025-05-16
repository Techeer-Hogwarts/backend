package backend.techeerzip.infra.slack;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import backend.techeerzip.global.logger.CustomLogger;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SlackEventHandler {

    @Value("${SLACKBOT_URL}")
    private String channelUrl;

    @Value("${SLACKBOT_PERSONAL_URL}")
    private String dmUrl;

    private final RestTemplate restTemplate;
    private final CustomLogger log;

    @Async("taskExecutor")
    @EventListener
    public void sendChannel(SlackEvent.Channel<?> event) {
        try {
            log.debug("SlackEvent Channel 요청 보냄: " + channelUrl);
            restTemplate.postForEntity(channelUrl, event.getPayload(), Void.class);
        } catch (Exception e) {
            log.error("인덱스 생성 중 오류 발생: " + channelUrl + e.getMessage());
        }
    }

    @Async("taskExecutor")
    @EventListener
    public void sendDM(SlackEvent.DM<?> event) {
        try {
            log.debug("SlackEvent DM 요청 보냄: " + dmUrl);
            restTemplate.postForEntity(dmUrl, event.getPayload(), Void.class);
        } catch (Exception e) {
            log.error("인덱스 삭제 중 오류 발생: " + dmUrl + e.getMessage());
        }
    }
}
