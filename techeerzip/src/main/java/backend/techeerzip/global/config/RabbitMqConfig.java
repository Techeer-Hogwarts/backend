package backend.techeerzip.global.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {
    public static final String CRAWL_QUEUE = "crawl_queue";
    public static final String CRAWL_EXCHANGE = "crawl.exchange";
    public static final String CRAWL_ROUTING_KEY = "crawl.#";

    @Value("${spring.rabbitmq.addresses}")
    private String uri;

    @Bean
    public Queue crawlQueue() {
        return new Queue(CRAWL_QUEUE, true);
    }

    @Bean
    public TopicExchange crawlExchange() {
        return new TopicExchange(CRAWL_EXCHANGE);
    }

    @Bean
    public Binding crawlBinding(Queue crawlQueue, TopicExchange crawlExchange) {
        return BindingBuilder.bind(crawlQueue).to(crawlExchange).with(CRAWL_ROUTING_KEY);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplate;
    }
}
