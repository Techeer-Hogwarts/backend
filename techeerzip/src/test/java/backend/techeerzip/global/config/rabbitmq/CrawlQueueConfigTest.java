package backend.techeerzip.global.config.rabbitmq;

import static org.junit.jupiter.api.Assertions.*;
import backend.techeerzip.infra.rabbitmq.type.MqResource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.amqp.core.Queue;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = CrawlQueueConfig.class)
class CrawlQueueConfigTest {

    @Autowired
    private Queue crawlQueue;

    @Autowired
    private TopicExchange crawlExchange;

    @Autowired
    private Binding crawlBinding;

    @Test
    void crawlQueueShouldUseMqResourceQueueName() {
        assertEquals(MqResource.CRAWL.queue(), crawlQueue.getName());
    }

    @Test
    void crawlExchangeShouldUseMqResourceExchangeName() {
        assertEquals(MqResource.CRAWL.exchange(), crawlExchange.getName());
    }

    @Test
    void crawlBindingShouldBindQueueToExchangeWithCorrectRoutingKey() {
        assertEquals(crawlQueue.getName(), crawlBinding.getDestination());
        assertEquals(crawlExchange.getName(), crawlBinding.getExchange());
        assertEquals(MqResource.CRAWL.routingKey(), crawlBinding.getRoutingKey());
    }
}
