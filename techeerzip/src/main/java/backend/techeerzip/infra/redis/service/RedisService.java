package backend.techeerzip.infra.redis.service;

import backend.techeerzip.global.logger.CustomLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {
    private static final String CONTEXT = "RedisService";
    private final RedisTemplate<String, String> redisTemplate;
    private final RedisMessageListenerContainer redisMessageListenerContainer;
    private final CustomLogger logger;

    /**
     * 작업 상태 저장
     */
    public void setTaskStatus(String taskId, String task) {
        try {
            redisTemplate.opsForHash().putAll(taskId, Map.of(
                "task", task,
                "result", "pending",
                "processed", "false"
            ));
            redisTemplate.expire(taskId, 3600, TimeUnit.SECONDS); // expire 1시간 = 3600초
            logger.debug(String.format("Task status set for taskId: %s", taskId), CONTEXT);
        } catch (Exception e) {
            logger.error("Failed to set task status for taskId: %s, error: %s", taskId, e.getMessage());
            throw e;
        }
    }

    /**
     * 작업 세부 정보 가져오기
     */
    public Map<Object, Object> getTaskDetails(String taskId) {
        try {
            Map<Object, Object> details = redisTemplate.opsForHash().entries(taskId);
            logger.debug(String.format("Retrieved task details for taskId: %s", taskId), CONTEXT);
            return details;
        } catch (Exception e) {
            logger.error("Failed to get task details for taskId: %s, error: %s", taskId, e.getMessage());
            throw e;
        }
    }

    /**
     * Redis Pub/Sub 채널 구독
     */
    public void subscribeToChannel(String channel, MessageListener messageListener) {
        try {
            ChannelTopic topic = new ChannelTopic(channel);
            redisMessageListenerContainer.addMessageListener((message, pattern) -> {
                String messageContent = new String(message.getBody());
                logger.debug(String.format("Message received on channel %s: %s", channel, messageContent), CONTEXT);
                messageListener.onMessage(messageContent);
            }, topic);
            logger.debug(String.format("Subscribed to channel: %s", channel), CONTEXT);
        } catch (Exception e) {
            logger.error("Failed to subscribe to channel: %s, error: %s", channel, e.getMessage());
            throw e;
        }
    }

    /**
     * 완료된 작업 삭제
     */
    public void deleteTask(String taskId) {
        try {
            Boolean deleted = redisTemplate.delete(taskId);
            if (Boolean.FALSE.equals(deleted)) {
                logger.error("Failed to delete task: %s, task not found", taskId, CONTEXT);
            } else {
                logger.debug(String.format("Successfully deleted task: %s", taskId), CONTEXT);
            }
        } catch (Exception e) {
            logger.error("Failed to delete task: %s, error: %s", taskId, CONTEXT);
            throw e;
        }
    }

    /**
     * 메시지 리스너 인터페이스
     */
    @FunctionalInterface
    public interface MessageListener {
        void onMessage(String message);
    }
} 