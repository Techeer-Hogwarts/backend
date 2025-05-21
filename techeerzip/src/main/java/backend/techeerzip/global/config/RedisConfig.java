package backend.techeerzip.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

// import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class RedisConfig {

    // @Value("${spring.data.redis.host}")
    private String redisHost = "localhost"; // Docker 컨테이너 외부에서 테스트할 때는 localhost

    // @Value("${spring.data.redis.port}")
    private int redisPort = 6379;

    // @Value("${spring.data.redis.password}")
    private String redisPassword = "1234";

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        try {
            RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
            redisConfig.setHostName(redisHost);
            redisConfig.setPort(redisPort);
            redisConfig.setPassword(redisPassword);

            LettuceConnectionFactory factory = new LettuceConnectionFactory(redisConfig);
            factory.setValidateConnection(true);
            factory.setShareNativeConnection(false);

            // 연결 테스트 로그 추가
            System.out.println(
                    "Redis connection attempt - host: " + redisHost + ", port: " + redisPort);

            factory.afterPropertiesSet();
            return factory;
        } catch (Exception e) {
            System.err.println(
                    "Redis connection failed - host: " + redisHost + ", port: " + redisPort);
            e.printStackTrace();
            throw new RuntimeException("Failed to create Redis connection", e);
        }
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate() {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());

        // 직렬화 설정
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setValueSerializer(stringSerializer);
        redisTemplate.setHashKeySerializer(stringSerializer);
        redisTemplate.setHashValueSerializer(stringSerializer);

        // 연결 테스트
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        // // 스레드 풀 설정
        // ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // executor.setCorePoolSize(2);
        // executor.setMaxPoolSize(4);
        // executor.setQueueCapacity(100);
        // executor.setThreadNamePrefix("redis-listener-");
        // executor.initialize();

        // container.setTaskExecutor(executor);
        return container;
    }
}
