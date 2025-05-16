package backend.techeerzip.global.config.rabbitmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import backend.techeerzip.global.logger.CustomLogger;
import backend.techeerzip.infra.rabbitmq.type.MqResource;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RabbitMqSender {

    private final RabbitTemplate rabbitTemplate;
    private final CustomLogger log;

    public void send(MqResource mqResource, String taskId, String task, String type) {
        try {
            rabbitTemplate.convertAndSend(
                    mqResource.exchange(),
                    mqResource.routingKey(),
                    task,
                    message -> {
                        message.getMessageProperties().setMessageId(taskId);
                        message.getMessageProperties().setType(type);
                        message.getMessageProperties().setContentType("text/plain");
                        return message;
                    });
            log.debug(String.format("Sent task: %s - type: %s", task, type), mqResource.context());
        } catch (Exception e) {
            log.error("Failed to send task: {}, error: {}", taskId, e.getMessage());
            throw e;
        }
    }
}
