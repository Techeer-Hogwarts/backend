package backend.techeerzip.domain.task.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.techeerzip.domain.blog.dto.request.BlogSaveRequest;
import backend.techeerzip.domain.blog.dto.response.BlogUrlsResponse;
import backend.techeerzip.domain.blog.dto.response.CrawlingBlogResponse;
import backend.techeerzip.domain.blog.entity.BlogCategory;
import backend.techeerzip.domain.blog.exception.BlogEmptyDateException;
import backend.techeerzip.domain.blog.exception.BlogEmptyPostsException;
import backend.techeerzip.domain.blog.exception.BlogInvalidDateFormatException;
import backend.techeerzip.domain.blog.service.BlogService;
import backend.techeerzip.domain.task.entity.TaskType;
import backend.techeerzip.global.logger.CustomLogger;
import backend.techeerzip.infra.rabbitmq.service.RabbitMqService;
import backend.techeerzip.infra.redis.service.RedisService;

@Service
public class TaskService {
    private static final String CONTEXT = "TaskService";
    private final RabbitMqService rabbitMQService;
    private final RedisService redisService;
    private final @Lazy BlogService blogService;
    private final CustomLogger logger;

    @Autowired
    @Lazy
    private TaskService self; // 순환 참조 해결을 위한 자기 주입

    public TaskService(
            RabbitMqService rabbitMQService,
            RedisService redisService,
            @Lazy BlogService blogService,
            CustomLogger logger) {
        this.rabbitMQService = rabbitMQService;
        this.redisService = redisService;
        this.blogService = blogService;
        this.logger = logger;
    }

    @PostConstruct
    public void init() {
        logger.info("TaskService 초기화 중", CONTEXT);

        subscribeToTaskCompletion(TaskType.SIGNUP_BLOG_FETCH.getValue());
        subscribeToTaskCompletion(TaskType.DAILY_UPDATE.getValue());
        subscribeToTaskCompletion(TaskType.SHARED_POST_FETCH.getValue());

        logger.info("채널 구독 시작 : SIGNUP_BLOG_FETCH, DAILY_UPDATE, SHARED_POST_FETCH", CONTEXT);
    }

    private void subscribeToTaskCompletion(String queueName) {
        logger.info("Subscribing to queue: {}", queueName);
        redisService.subscribeToChannel(
                queueName,
                taskId -> {
                    logger.info(
                            "Received task completion notification - queue: {}, taskId: {}",
                            queueName,
                            taskId);

                    Map<Object, Object> taskDetails = redisService.getTaskDetails(taskId);
                    logger.info(
                            "Retrieved task details - taskId: {}, details: {}",
                            taskId,
                            taskDetails);

                    if (taskDetails == null || taskDetails.get("result") == null) {
                        logger.error(
                                "Task details not found or result is null - taskId: {}", taskId);
                        return;
                    }

                    String taskData = (String) taskDetails.get("result");
                    logger.info("Processing task - taskId: {}, data: {}", taskId, taskData);

                    processTask(taskId, taskData, TaskType.fromValue(queueName));
                });
    }

    /** 매일 새벽 3시 - 유저 최신 블로그 게시물 크롤링 요청 */
    @Scheduled(cron = "0 0 3 * * *")
    public void requestDailyUpdate() {
        logger.info("일일 블로그 업데이트 요청 시작", CONTEXT);
        List<BlogUrlsResponse> userBlogUrls = blogService.getAllUserBlogUrl();
        logger.info(String.format("userBlogUrls: %s", userBlogUrls), CONTEXT);

        userBlogUrls.forEach(
                user -> user.getBlogUrls()
                        .forEach(
                                url -> {
                                    if (url == null || url.trim().isEmpty()) {
                                        logger.error("Cannot send an empty task.");
                                        return;
                                    }
                                    String taskId = generateTaskId(
                                            "blogs_daily_update", user.getUserId());
                                    rabbitMQService.sendToQueue(
                                            taskId, url, "blogs_daily_update");
                                    logger.info("Sending task: {} - {}", taskId, url);
                                    redisService.setTaskStatus(taskId, url);
                                }));
    }

    /** 매일 새벽 3시 - 유저 최신 블로그 게시물 크롤링 응답 후 처리 */
    @Transactional
    public void processDailyUpdate(String taskId, String taskData) {
        logger.info("Performing daily update for task {}: {}", taskId, taskData, CONTEXT);
        try {
            // taskData(JSON) → DTO, 카테고리는 고정 예시로 TECHEER 지정
            CrawlingBlogResponse blogs = new CrawlingBlogResponse(taskData, BlogCategory.TECHEER);

            // 필터링 후 DTO에 반영
            List<BlogSaveRequest> filtered = filterPosts(blogs.getPosts());
            blogs.updatePosts(filtered);

            logger.info("필터링한 블로그 생성 요청 중 - posts: {}", blogs.getPosts(), CONTEXT);
            blogService.createBlog(blogs);
        } catch (Exception e) {
            handleTaskError(taskId, "blogs_daily_update", e);
        } finally {
            logger.info("블로그 생성 후 태스크 삭제", CONTEXT);
            redisService.deleteTask(taskId);
        }
    }

    // 현재 시간 기준 24시간 동안의 글 필터링
    private List<BlogSaveRequest> filterPosts(List<BlogSaveRequest> posts) {
        if (posts == null) {
            logger.warn("Posts list is null");
            throw new BlogEmptyPostsException();
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start24hAgo = now.minusHours(24);

        logger.info("Current time: {}, 24 hours ago: {}", now, start24hAgo);

        List<BlogSaveRequest> filtered = posts.stream()
                .filter(Objects::nonNull)
                .filter(
                        post -> {
                            String dateStr = post.getDate();
                            if (dateStr == null || dateStr.trim().isEmpty()) {
                                logger.warn(
                                        "Post date is null or empty for post: {}",
                                        post.getTitle());
                                throw new BlogEmptyDateException(post.getTitle());
                            }

                            try {
                                LocalDateTime postDate = LocalDateTime.parse(
                                        dateStr, DateTimeFormatter.ISO_DATE_TIME);
                                logger.debug(
                                        "Post date: {} | context: {}", postDate, CONTEXT);
                                return !postDate.isBefore(start24hAgo)
                                        && !postDate.isAfter(now);
                            } catch (DateTimeParseException e) {
                                logger.warn(
                                        "Invalid date format for post '{}': {}",
                                        post.getTitle(),
                                        dateStr);
                                throw new BlogInvalidDateFormatException(
                                        post.getTitle(), dateStr);
                            }
                        })
                .collect(Collectors.toList());

        logger.info("Filtered {} posts from last 24 hours | context: {}", filtered.size(), CONTEXT);
        return filtered;
    }

    /** 신규 유저의 최신 게시물 저장 요청 */
    @Transactional
    public void requestSignUpBlogFetch(Long userId, String url) {
        String taskId = generateTaskId("signUp_blog_fetch", userId);
        rabbitMQService.sendToQueue(taskId, url, "signUp_blog_fetch");
        logger.info("Sending task: {} - {}", taskId, url);
        redisService.setTaskStatus(taskId, url);
        logger.info(String.format("Received task: %s", url), CONTEXT);
    }

    /** 신규 유저의 최신 게시물 저장 */
    @Transactional
    public void processSignUpBlogFetch(String taskId, String taskData) {
        logger.info(String.format("Fetching all blogs for task %s: %s", taskId, taskData), CONTEXT);
        try {
            CrawlingBlogResponse blogs = new CrawlingBlogResponse(taskData, BlogCategory.TECHEER);
            logger.info(String.format("신규 유저의 블로그 생성 요청 중 - posts: %s", blogs.getPosts()), CONTEXT);
            blogService.createBlog(blogs);
        } catch (Exception e) {
            handleTaskError(taskId, "signUp_blog_fetch", e);
        } finally {
            logger.info("블로그 생성 후 테스크 삭제", CONTEXT);
            redisService.deleteTask(taskId);
        }
    }

    /** shared 외부 게시물 저장 요청 */
    @Transactional
    public void requestSharedPostFetch(Long userId, String url) {
        String taskId = generateTaskId("shared_post_fetch", userId);
        logger.info("Requesting shared post fetch - taskId: {}, url: {}", taskId, url);

        rabbitMQService.sendToQueue(taskId, url, "shared_post_fetch");
        logger.info("Sent to RabbitMQ - taskId: {}", taskId);

        redisService.setTaskStatus(taskId, url);
        logger.info("Set Redis status - taskId: {}", taskId);

        // Redis 상태 확인
        Map<Object, Object> taskDetails = redisService.getTaskDetails(taskId);
        logger.info("Initial Redis task details - taskId: {}, details: {}", taskId, taskDetails);
    }

    /** shared 외부 게시물 저장 */
    @Transactional
    public void processSharedPostFetch(String taskId, String taskData) {
        logger.info(
                String.format("Fetching post details for task %s: %s", taskId, taskData), CONTEXT);
        try {
            CrawlingBlogResponse post = new CrawlingBlogResponse(taskData, BlogCategory.SHARED);
            logger.info(String.format("외부 블로그 생성 요청 중 - posts: %s", post), CONTEXT);
            blogService.createBlog(post);
        } catch (Exception e) {
            handleTaskError(taskId, "shared_post_fetch", e);
        } finally {
            logger.info("블로그 생성 후 테스크 삭제", CONTEXT);
            redisService.deleteTask(taskId);
        }
    }

    /** 태스크 ID 생성 */
    private String generateTaskId(String type, Long userId) {
        return String.format("task-%s-%d-%d", type, System.currentTimeMillis(), userId);
    }

    @Transactional
    public void processTask(String taskId, String taskData, TaskType type) {
        switch (type) {
            case SIGNUP_BLOG_FETCH -> processSignUpBlogFetch(taskId, taskData);
            case DAILY_UPDATE -> processDailyUpdate(taskId, taskData);
            case SHARED_POST_FETCH -> processSharedPostFetch(taskId, taskData);
        }
    }

    private void handleTaskError(String taskId, String type, Exception e) {
        logger.error("Task failed - id: {}, type: {}, error: {}", taskId, type, e.getMessage());
        // 추가적인 에러 처리 (예: 재시도 로직, 알림 등)
    }
}
