package backend.techeerzip.infra.rabbitmq.service;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import backend.techeerzip.global.config.RabbitMqConfig;
import backend.techeerzip.global.logger.CustomLogger;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RabbitMqService {
    private static final String CONTEXT = "RabbitMqService";
    private final RabbitTemplate rabbitTemplate;
    private final CustomLogger logger;

    /** 큐에 메시지 전송 */
    public void sendToQueue(String taskId, String task, String type) {
        try {
            String encodedUrl = Base64.getEncoder().encodeToString(task.getBytes());
            Map<String, String> blogRequest = new HashMap<>();
            blogRequest.put("data", encodedUrl); // base64로 인코딩된 URL
            blogRequest.put("user_id", extractUserId(taskId));

            rabbitTemplate.convertAndSend(
                    RabbitMqConfig.CRAWL_QUEUE,
                    blogRequest,
                    message -> {
                        message.getMessageProperties().setMessageId(taskId);
                        message.getMessageProperties().setType(type);
                        message.getMessageProperties().setContentType("application/json");
                        return message;
                    });
            logger.info(String.format("Sent task: %s - type: %s", task, type), CONTEXT);
        } catch (Exception e) {
            logger.error("Failed to send task: {}, error: {}", taskId, e.getMessage());
            throw e;
        }
    }

    /** taskId에서 user_id 추출 */
    private String extractUserId(String taskId) {
        // taskId 형식: "task-{type}-{timestamp}-{userId}"
        String[] parts = taskId.split("-");
        return parts[parts.length - 1]; // 마지막 부분이 userId
    }
}
