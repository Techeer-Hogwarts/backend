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
    // 이 설정은 MqResource.CRAWL enum 값에 직접 의존합니다.
    // enum 값(queue, exchange, routingKey)을 변경하면 바인딩 설정도 함께 수정해야 합니다.
    // 반드시 CrawlQueueConfigTest 테스트를 통과시켜 enum 변경이 설정에 영향을 주지 않도록 확인하세요.
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
