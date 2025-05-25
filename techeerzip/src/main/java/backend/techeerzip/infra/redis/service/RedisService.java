package backend.techeerzip.infra.redis.service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import jakarta.annotation.PostConstruct;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;

import backend.techeerzip.global.logger.CustomLogger;
import backend.techeerzip.infra.redis.exception.RedisMessageProcessingException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisService {
    private static final String CONTEXT = "RedisService";
    private final RedisTemplate<String, String> redisTemplate;
    private final RedisMessageListenerContainer redisMessageListenerContainer;
    private final CustomLogger logger;

    @PostConstruct
    public void init() {
        try {
            // Redis 연결 테스트
            if (redisTemplate == null || redisTemplate.getConnectionFactory() == null) {
                throw new IllegalStateException(
                        "Redis 연결 팩토리가 올바르게 초기화되지 않았습니다.");
            }
            redisTemplate.getConnectionFactory().getConnection().ping();
            logger.info("Redis 연결 성공", CONTEXT);
        } catch (Exception e) {
            logger.error("Redis 연결 실패: {}", e.getMessage());
            throw new RuntimeException("Redis 연결 초기화 실패", e);
        }
    }

    /** 작업 상태 저장 */
    public void setTaskStatus(String taskId, String task) {
        try {
            redisTemplate
                    .opsForHash()
                    .putAll(
                            taskId,
                            Map.of(
                                    "task", task,
                                    "result", "pending",
                                    "processed", "false"));
            redisTemplate.expire(taskId, 3600, TimeUnit.SECONDS); // expire 1시간 = 3600초
            logger.info("작업 상태 설정 완료 - taskId: {}", taskId, CONTEXT);
        } catch (Exception e) {
            logger.error(
                    "작업 상태 설정 실패 - taskId: {}, 오류: {}", taskId, e.getMessage());
            throw e;
        }
    }

    /** 작업 세부 정보 가져오기 */
    public Map<Object, Object> getTaskDetails(String taskId) {
        try {
            Map<Object, Object> details = redisTemplate.opsForHash().entries(taskId);
            logger.info("작업 상세 정보 조회 완료 - taskId: {}", taskId, CONTEXT);
            return details;
        } catch (Exception e) {
            logger.error(
                    "작업 상세 정보 조회 실패 - taskId: {}, 오류: {}", taskId, e.getMessage());
            throw e;
        }
    }

    /** Redis Pub/Sub 채널 구독 */
    public void subscribeToChannel(String channel, MessageListener messageListener) {
        try {
            ChannelTopic topic = new ChannelTopic(channel);
            redisMessageListenerContainer.addMessageListener(
                    (message, pattern) -> {
                        try {
                            String messageContent = new String(message.getBody());
                            logger.info("채널 {}에서 메시지 수신: {}", channel, messageContent);
                            messageListener.onMessage(messageContent);
                        } catch (Exception e) {
                            logger.error("채널 {}의 메시지 처리 중 오류 발생: {}", channel, e.getMessage(), e);
                            throw new RedisMessageProcessingException(
                                    String.format("채널 %s의 메시지 처리 실패: %s", channel,
                                            e.getMessage()));
                        }
                    },
                    topic);
            logger.info("채널 구독 성공: {}", channel);
        } catch (Exception e) {
            logger.error("채널 구독 실패: {}, 오류: {}", channel, e.getMessage());
            throw new RedisMessageProcessingException(
                    String.format("채널 %s 구독 실패: %s", channel, e.getMessage()));
        }
    }

    /** 완료된 작업 삭제 */
    public void deleteTask(String taskId) {
        try {
            Boolean deleted = redisTemplate.delete(taskId);
            if (Boolean.FALSE.equals(deleted)) {
                logger.error("작업 삭제 실패 - taskId: {}, 작업을 찾을 수 없음", taskId, CONTEXT);
            } else {
                logger.info("작업 삭제 성공 - taskId: {}", taskId, CONTEXT);
            }
        } catch (Exception e) {
            logger.error("작업 삭제 실패 - taskId: {}, 오류: {}", taskId, e.getMessage(), CONTEXT);
            throw e;
        }
    }

    /** 메시지 리스너 인터페이스 */
    @FunctionalInterface
    public interface MessageListener {
        void onMessage(String message);
    }
}
