package backend.techeerzip.infra.slack;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SlackEventHandler {

    @Value("${SLACKBOT_URL}")
    private String channelUrl;

    @Value("${SLACKBOT_PERSONAL_URL}")
    private String dmUrl;

    private final RestTemplate restTemplate;

    @Async("defaultExecutor")
    @EventListener
    public void sendChannel(SlackEvent.Channel<?> event) {
        try {
            log.debug("SlackEvent Channel 요청 보냄: " + dmUrl);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String jsonPayload = new ObjectMapper().writeValueAsString(event.getPayload());
            log.debug("SlackEvent Channel 전송 Request\n {}", jsonPayload);
            HttpEntity<String> request = new HttpEntity<>(jsonPayload, headers);
            restTemplate.postForEntity(channelUrl, request, Void.class);
        } catch (Exception e) {
            log.error("SlackEvent Channel 메시지 전송 중 오류 발생: " + channelUrl + e.getMessage());
        }
    }

    @Async("defaultExecutor")
    @EventListener
    public void sendDM(SlackEvent.DM<?> event) {
        try {
            log.debug("SlackEvent DM 요청 보냄: {}", dmUrl);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String jsonPayload = new ObjectMapper().writeValueAsString(event.getPayload());
            HttpEntity<String> request = new HttpEntity<>(jsonPayload, headers);
            log.debug("SlackEvent DM 전송 Request\n {}", jsonPayload);
            restTemplate.postForEntity(dmUrl, request, Void.class);
        } catch (Exception e) {
            log.error("Slack DM 메시지 전송 중 오류 발생: " + dmUrl + " " + e.getMessage(), e);
        }
    }
}
