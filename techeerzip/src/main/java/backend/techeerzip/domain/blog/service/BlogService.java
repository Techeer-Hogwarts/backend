package backend.techeerzip.domain.blog.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.techeerzip.domain.blog.dto.request.BlogQueryRequest;
import backend.techeerzip.domain.blog.dto.response.BlogListResponse;
import backend.techeerzip.domain.blog.dto.response.BlogResponse;
import backend.techeerzip.domain.blog.dto.response.BlogUrlsResponse;
import backend.techeerzip.domain.blog.dto.response.CrawlingBlogResponse;
import backend.techeerzip.domain.blog.entity.Blog;
import backend.techeerzip.domain.blog.exception.BlogNotFoundException;
import backend.techeerzip.domain.blog.repository.BlogRepository;
import backend.techeerzip.domain.task.service.TaskService;
import backend.techeerzip.domain.user.entity.User;
import backend.techeerzip.domain.user.repository.UserRepository;
import backend.techeerzip.global.logger.CustomLogger;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BlogService {
    private final BlogRepository blogRepository;
    private final UserRepository userRepository;
    private final TaskService taskService;
    private final CustomLogger logger;
    private static final String CONTEXT = "BlogService";
    private static final DateTimeFormatter ISO_DATE_TIME = DateTimeFormatter.ISO_DATE_TIME;

    @Transactional
    public void createSharedBlog(Long userId, String url) {
        logger.debug(String.format("외부 블로그 게시 요청 중 - userId: %d, url: %s", userId, url), CONTEXT);
        taskService.requestSharedPostFetch(userId, url);
    }

    public BlogListResponse getBlogList(BlogQueryRequest query) {
        logger.debug(
                "블로그 목록 조회 - cursorId: {}, category: {}, sortBy: {}, limit: {}",
                query.getCursorId(),
                query.getCategory(),
                query.getSortBy(),
                query.getLimit());

        List<Blog> blogs =
                blogRepository.findBlogsWithCursor(
                        query.getCursorId(),
                        query.getCategory(),
                        query.getSortBy(),
                        query.getLimit());

        List<BlogResponse> blogResponses =
                blogs.stream().map(BlogResponse::new).collect(Collectors.toList());

        logger.debug("블로그 목록 조회 성공 - size: {}", blogResponses.size());
        return new BlogListResponse(blogResponses, query.getLimit());
    }

    public BlogListResponse getBlogsByUser(Long userId, Long cursorId, int limit) {
        logger.debug(
                "유저별 블로그 목록 조회 - userId: {}, cursorId: {}, limit: {}", userId, cursorId, limit);

        List<Blog> blogs = blogRepository.findUserBlogsWithCursor(userId, cursorId, limit);

        List<BlogResponse> blogResponses =
                blogs.stream().map(BlogResponse::new).collect(Collectors.toList());

        logger.debug("유저별 블로그 목록 조회 성공 - size: {}", blogResponses.size());
        return new BlogListResponse(blogResponses, limit);
    }

    public BlogListResponse getBestBlogs(Long cursorId, int limit) {
        logger.debug("인기 블로그 목록 조회 - cursorId: {}, limit: {}", cursorId, limit);

        List<Blog> blogs = blogRepository.findPopularBlogsWithCursor(cursorId, limit);

        List<BlogResponse> blogResponses =
                blogs.stream().map(BlogResponse::new).collect(Collectors.toList());

        logger.debug("인기 블로그 목록 조회 성공 - size: {}", blogResponses.size());
        return new BlogListResponse(blogResponses, limit);
    }

    public BlogResponse getBlog(Long blogId) {
        logger.debug(String.format("블로그 ID %d 조회 요청", blogId), CONTEXT);

        Blog blog = blogRepository.findByIdAndIsDeletedFalse(blogId);
        if (blog == null) {
            logger.warn(String.format("블로그 조회 실패 - 존재하지 않는 blogId: %d", blogId), CONTEXT);
            throw new BlogNotFoundException();
        }

        logger.debug("단일 블로그 엔티티 목록 조회 성공", CONTEXT);
        return new BlogResponse(blog);
    }

    @Transactional
    public void increaseBlogViewCount(Long blogId) {
        logger.debug(String.format("블로그 조회수 증가 처리 중 - blogId: %d", blogId), CONTEXT);

        Blog blog = blogRepository.findByIdAndIsDeletedFalse(blogId);
        if (blog == null) {
            logger.warn(String.format("블로그 조회수 증가 실패 - 존재하지 않는 blogId: %d", blogId), CONTEXT);
            throw new BlogNotFoundException();
        }

        blog.increaseViewCount();
        logger.debug(String.format("블로그 조회수 증가 성공 - viewCount: %d", blog.getViewCount()), CONTEXT);
    }

    @Transactional
    public BlogResponse deleteBlog(Long blogId) {
        logger.debug(String.format("블로그 ID %d 삭제 요청", blogId), CONTEXT);
        Blog blog = blogRepository.findByIdAndIsDeletedFalse(blogId);
        logger.info("블로그 삭제 요청 - blogId: {}", blogId);
        if (blog == null) {
            logger.warn(String.format("블로그 삭제 실패 - 존재하지 않거나 이미 삭제된 blogId: %d", blogId), CONTEXT);
            throw new BlogNotFoundException();
        }
        blog.softDelete();
        logger.debug("블로그 삭제 성공", CONTEXT);
        return new BlogResponse(blog);
    }

    public List<BlogUrlsResponse> getAllUserBlogUrl() {
        logger.debug("모든 유저의 블로그 url 조회 처리 중", CONTEXT);
        List<BlogUrlsResponse> result =
                userRepository.findByIsDeletedFalse().stream()
                        .map(
                                user ->
                                        new BlogUrlsResponse(
                                                user.getId(),
                                                List.of(
                                                                user.getTistoryUrl(),
                                                                user.getMediumUrl(),
                                                                user.getVelogUrl())
                                                        .stream()
                                                        .filter(
                                                                url ->
                                                                        url != null
                                                                                && !url.trim()
                                                                                        .isEmpty())
                                                        .filter(
                                                                url ->
                                                                        url != null
                                                                                && !url.trim()
                                                                                        .isEmpty())
                                                        .collect(Collectors.toList())))
                        .collect(Collectors.toList());
        logger.debug("모든 유저의 블로그 url 조회 성공", CONTEXT);
        return result;
    }

    @Transactional
    public void createBlog(CrawlingBlogResponse dto) {
        logger.info("블로그 데이터 저장 시작 - userId: {}, posts: {}", dto.getUserId(), dto.getPosts());

        User author =
                userRepository
                        .findById(dto.getUserId())
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                "User not found: " + dto.getUserId()));
        logger.info("블로그 작성자 정보 조회 성공 - userId: {}", author.getId());

        dto.getPosts()
                .forEach(
                        post -> {
                            try {
                                logger.info(
                                        "블로그 저장 시도 - title: {}, url: {}",
                                        post.getTitle(),
                                        post.getUrl());

                                // ISO 8601 형식의 날짜 파싱
                                LocalDateTime postDate;
                                try {
                                    postDate = LocalDateTime.parse(post.getDate(), ISO_DATE_TIME);
                                } catch (DateTimeParseException e) {
                                    logger.warn(
                                            "날짜 파싱 실패, 현재 시간으로 대체 - date: {}, error: {}",
                                            post.getDate(),
                                            e.getMessage());
                                    postDate = LocalDateTime.now();
                                }

                                Blog blog =
                                        Blog.builder()
                                                .user(author)
                                                .title(post.getTitle())
                                                .url(post.getUrl())
                                                .date(postDate)
                                                .author(post.getAuthor())
                                                .authorImage(post.getAuthorImage())
                                                .category(
                                                        dto.getCategory() != null
                                                                ? dto.getCategory().name()
                                                                : null)
                                                .thumbnail(post.getThumbnail())
                                                .tags(post.getTags())
                                                .build();

                                blogRepository.save(blog);
                                logger.info(
                                        "블로그 저장 성공 - id: {}, title: {}",
                                        blog.getId(),
                                        blog.getTitle());
                            } catch (Exception e) {
                                logger.error(
                                        "블로그 저장 실패 - title: {}, url: {}, error: {}",
                                        post.getTitle(),
                                        post.getUrl(),
                                        e.getMessage(),
                                        e);
                            }
                        });
    }
}
