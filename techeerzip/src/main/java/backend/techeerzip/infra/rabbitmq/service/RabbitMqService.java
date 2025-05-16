package backend.techeerzip.infra.rabbitmq.service;

import org.springframework.stereotype.Service;

import backend.techeerzip.global.config.rabbitmq.RabbitMqSender;
import backend.techeerzip.infra.rabbitmq.type.MqResource;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RabbitMqService {

    private final RabbitMqSender sender;

    /** 큐에 메시지 전송 */
    public void sendCrawlTask(String taskId, String task, String type) {
        sender.send(MqResource.CRAWL, taskId, task, type);
    }
}
