package backend.techeerzip.domain.task.service;

import backend.techeerzip.domain.blog.dto.request.BlogSaveRequest;
import backend.techeerzip.domain.blog.dto.response.BlogUrlsResponse;
import backend.techeerzip.domain.blog.dto.response.CrawlingBlogResponse;
import backend.techeerzip.domain.blog.entity.BlogCategory;
import backend.techeerzip.domain.blog.service.BlogService;
import backend.techeerzip.global.logger.CustomLogger;
import backend.techeerzip.infra.redis.service.RedisService;
import backend.techeerzip.infra.rabbitmq.service.RabbitMqService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TaskService {
    private static final String CONTEXT = "TaskService";
    private final RabbitMqService rabbitMQService;
    private final RedisService redisService;
    private final @Lazy BlogService blogService;
    private final CustomLogger logger;

    @Autowired
    @Lazy
    private TaskService self;

    public TaskService(RabbitMqService rabbitMQService,
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

        // 3개의 채널 구독
        subscribeToTaskCompletion("signUp_blog_fetch");
        subscribeToTaskCompletion("blogs_daily_update");
        subscribeToTaskCompletion("shared_post_fetch");

        logger.info("signUp_blog_fetch, blogs_daily_update, shared_post_fetch 채널 구독 시작" + CONTEXT);
    }

    private void subscribeToTaskCompletion(String queueName) {
        logger.info("구독 시작 - queueName: " + queueName + CONTEXT);
        redisService.subscribeToChannel(queueName, taskId -> {
            logger.info(String.format("작업 완료 알림 수신 - queueName: %s, taskId: %s", queueName, taskId), CONTEXT);
            Map<Object, Object> taskDetails = redisService.getTaskDetails(taskId);
            if (taskDetails == null || taskDetails.get("result") == null) {
                logger.error(String.format("Task details not found for ID: %s", taskId));
                return;
            }
            String taskData = (String) taskDetails.get("result");
            logger.info(String.format("Processing task from channel %s: %s", queueName, taskId), CONTEXT);

            // 실제 비즈니스 로직 처리
            if (queueName.equals("signUp_blog_fetch")) {
                self.processSignUpBlogFetch(taskId, taskData);
            } else if (queueName.equals("blogs_daily_update")) {
                self.processDailyUpdate(taskId, taskData);
            } else if (queueName.equals("shared_post_fetch")) {
                self.processSharedPostFetch(taskId, taskData);
            }
            logger.info(String.format("Task %s completed.", taskId), CONTEXT);
        });
    }

    /**
     * 매일 새벽 3시 - 유저 최신 블로그 게시물 크롤링 요청
     */
    @Scheduled(cron = "0 0 3 * * *")
    public void requestDailyUpdate() {
        logger.info("일일 블로그 업데이트 요청 시작", CONTEXT);
        List<BlogUrlsResponse> userBlogUrls = blogService.getAllUserBlogUrl();
        logger.info(String.format("userBlogUrls: %s", userBlogUrls), CONTEXT);

        userBlogUrls.forEach(user -> user.getBlogUrls().forEach(url -> {
            if (url.trim().isEmpty()) {
                logger.error("Cannot send an empty task.");
                return;
            }
            String taskId = String.format("task-%d:%d-%d",
                    System.currentTimeMillis(), user.getUserId(), user.getBlogUrls().indexOf(url));
            rabbitMQService.sendToQueue(taskId, url, "blogs_daily_update");
            logger.info(String.format("Sending task: %s - %s", taskId, url), CONTEXT);
            redisService.setTaskStatus(taskId, url);
        }));
    }

    /**
     * 매일 새벽 3시 - 유저 최신 블로그 게시물 크롤링 응답 후 처리
     */
    @Transactional
    public void processDailyUpdate(String taskId, String taskData) {
        logger.info("Performing daily update for task {}: {}" + taskId + taskData, CONTEXT);
        try {
            // taskData(JSON) → DTO, 카테고리는 고정 예시로 TECHEER 지정
            CrawlingBlogResponse blogs = new CrawlingBlogResponse(taskData, BlogCategory.TECHEER);

            // 필터링 후 DTO에 반영
            List<BlogSaveRequest> filtered = filterPosts(blogs.getPosts());
            blogs.updatePosts(filtered);

            logger.info("필터링한 블로그 생성 요청 중 - posts: {}" + blogs.getPosts(), CONTEXT);
            blogService.createBlog(blogs);
        } catch (Exception e) {
            logger.error("블로그 생성 중 오류 발생 - {}" + e.getMessage() + e);
        } finally {
            logger.info("블로그 생성 후 태스크 삭제", CONTEXT);
            redisService.deleteTask(taskId);
        }
    }


    // 현재 시간 기준 24시간 동안의 글 필터링
    private List<BlogSaveRequest> filterPosts(List<BlogSaveRequest> posts) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start24hAgo = now.minusHours(24);

        logger.info("Current time: {}, 24 hours ago: {} | context: {} " + now + start24hAgo, CONTEXT);

        List<BlogSaveRequest> filtered = posts.stream()
                .filter(post -> {
                    LocalDateTime postDate = post.getDate();
                    logger.info("postDate: {} | context: {}" + postDate, CONTEXT);
                    return !postDate.isBefore(start24hAgo) && !postDate.isAfter(now);
                })
                .collect(Collectors.toList());

        logger.info("Filtered {} posts from last 24 hours | context: {}" + filtered.size(), CONTEXT);
        return filtered;
    }

    /**
     * 신규 유저의 최신 게시물 저장 요청
     */
    @Transactional
    public void requestSignUpBlogFetch(Long userId, String url) {
        String taskId = String.format("task-%d-%d", System.currentTimeMillis(), userId);
        rabbitMQService.sendToQueue(taskId, url, "signUp_blog_fetch");
        logger.info(String.format("Sending task: %s - %s", taskId, url), CONTEXT);
        redisService.setTaskStatus(taskId, url);
        logger.info(String.format("Received task: %s", url), CONTEXT);
    }

    /**
     * 신규 유저의 최신 게시물 저장
     */
    @Transactional
    public void processSignUpBlogFetch(String taskId, String taskData) {
        logger.info(String.format("Fetching all blogs for task %s: %s", taskId, taskData), CONTEXT);
        try {
            CrawlingBlogResponse blogs = new CrawlingBlogResponse(taskData, BlogCategory.TECHEER);
            logger.info(String.format("신규 유저의 블로그 생성 요청 중 - posts: %s", blogs.getPosts()), CONTEXT);
            blogService.createBlog(blogs);
        } catch (Exception e) {
            logger.error(String.format("블로그 생성 중 오류 발생 - %s", e.getMessage()));
        } finally {
            logger.info("블로그 생성 후 테스크 삭제", CONTEXT);
            redisService.deleteTask(taskId);
        }
    }

    /**
     * shared 외부 게시물 저장 요청
     */
    @Transactional
    public void requestSharedPostFetch(Long userId, String url) {
        String taskId = String.format("task-%d-%d", System.currentTimeMillis(), userId);
        rabbitMQService.sendToQueue(taskId, url, "shared_post_fetch");
        logger.info("Sending task: %s - %s" +  taskId + url +  CONTEXT);
        redisService.setTaskStatus(taskId, url);
        logger.info("Received task: %s" + url + CONTEXT);
    }

    /**
     * shared 외부 게시물 저장
     */
    @Transactional
    public void processSharedPostFetch(String taskId, String taskData) {
        logger.info(String.format("Fetching post details for task %s: %s", taskId, taskData), CONTEXT);
        try {
            CrawlingBlogResponse post = new CrawlingBlogResponse(taskData, BlogCategory.SHARED);
            logger.info(String.format("외부 블로그 생성 요청 중 - posts: %s", post), CONTEXT);
            blogService.createBlog(post);
        } catch (Exception e) {
            logger.error(String.format("블로그 생성 중 오류 발생 - taskId: %s, error: %s", taskId, e.getMessage()));
        } finally {
            logger.info("블로그 생성 후 테스크 삭제", CONTEXT);
            redisService.deleteTask(taskId);
        }
    }
}