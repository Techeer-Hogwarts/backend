package backend.techeerzip.infra.rabbitmq.service;

import backend.techeerzip.global.logger.CustomLogger;
import backend.techeerzip.global.config.RabbitMqConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RabbitMqService {
    private static final String CONTEXT = "RabbitMqService";
    private final RabbitTemplate rabbitTemplate;
    private final CustomLogger logger;

    /**
     * 큐에 메시지 전송
     */
    public void sendToQueue(String taskId, String task, String type) {
        try {
            rabbitTemplate.convertAndSend(
                RabbitMqConfig.CRAWL_EXCHANGE,
                RabbitMqConfig.CRAWL_ROUTING_KEY,
                task,
                message -> {
                    message.getMessageProperties().setMessageId(taskId);
                    message.getMessageProperties().setType(type);
                    message.getMessageProperties().setContentType("text/plain");
                    return message;
                }
            );
            logger.info(String.format("Sent task: %s - type: %s", task, type), CONTEXT);
        } catch (Exception e) {
            logger.error(
                    "Failed to send task: {}, error: {}",
                    taskId,
                    e.getMessage()
            );
            throw e;
        }
    }
} 