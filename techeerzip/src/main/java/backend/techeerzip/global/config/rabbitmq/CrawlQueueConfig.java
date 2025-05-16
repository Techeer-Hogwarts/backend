package backend.techeerzip.global.config.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import backend.techeerzip.infra.rabbitmq.type.MqResource;

@Configuration
public class CrawlQueueConfig {

    @Bean
    public Queue crawlQueue() {
        return new Queue(MqResource.CRAWL.queue(), true);
    }

    @Bean
    public TopicExchange crawlExchange() {
        return new TopicExchange(MqResource.CRAWL.exchange());
    }

    @Bean
    public Binding crawlBinding(Queue crawlQueue, TopicExchange crawlExchange) {
        return BindingBuilder.bind(crawlQueue)
                .to(crawlExchange)
                .with(MqResource.CRAWL.routingKey());
    }
}
