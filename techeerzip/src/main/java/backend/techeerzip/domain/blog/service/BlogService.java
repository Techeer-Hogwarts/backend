package backend.techeerzip.domain.blog.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.techeerzip.domain.blog.dto.request.BlogBestQueryRequest;
import backend.techeerzip.domain.blog.dto.request.BlogListQueryRequest;
import backend.techeerzip.domain.blog.dto.request.BlogSaveRequest;
import backend.techeerzip.domain.blog.dto.response.BlogListResponse;
import backend.techeerzip.domain.blog.dto.response.BlogResponse;
import backend.techeerzip.domain.blog.dto.response.BlogUrlsResponse;
import backend.techeerzip.domain.blog.dto.response.CrawlingBlogResponse;
import backend.techeerzip.domain.blog.entity.Blog;
import backend.techeerzip.domain.blog.entity.BlogCategory;
import backend.techeerzip.domain.blog.exception.BlogAlreadyExistsException;
import backend.techeerzip.domain.blog.exception.BlogInvalidRequestException;
import backend.techeerzip.domain.blog.exception.BlogNotFoundException;
import backend.techeerzip.domain.blog.exception.BlogUnauthorizedException;
import backend.techeerzip.domain.blog.exception.BlogUserNotFoundException;
import backend.techeerzip.domain.blog.mapper.BlogMapper;
import backend.techeerzip.domain.blog.repository.BlogRepository;
import backend.techeerzip.domain.task.service.TaskService;
import backend.techeerzip.domain.user.entity.User;
import backend.techeerzip.domain.user.repository.UserRepository;
import backend.techeerzip.global.logger.CustomLogger;
import backend.techeerzip.infra.index.IndexEvent;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BlogService {
    private final BlogRepository blogRepository;
    private final UserRepository userRepository;
    private final TaskService taskService;
    private final CustomLogger logger;
    private final ApplicationEventPublisher eventPublisher;
    private static final String CONTEXT = "BlogService";
    private static final DateTimeFormatter ISO_DATE_TIME = DateTimeFormatter.ISO_DATE_TIME;

    @Transactional
    public void createSharedBlog(Long userId, String url) {
        logger.info(String.format("외부 블로그 게시 요청 중 - userId: %d, url: %s", userId, url), CONTEXT);
        taskService.requestSharedPostFetch(userId, url);
    }

    public BlogListResponse getBlogList(BlogListQueryRequest query) {
        logger.info(
                "블로그 목록 조회 - cursorId: {}, category: {}, sortBy: {}, limit: {}",
                query.cursorId(),
                query.category(),
                query.sortBy(),
                query.limit(),
                CONTEXT);
        List<Blog> blogs =
                blogRepository.findBlogsWithCursor(
                        query.cursorId(), query.category(), query.sortBy(), query.limit());
        logger.info("블로그 목록 조회 성공 - size: {}", blogs.size(), CONTEXT);
        return BlogMapper.toListResponse(blogs, query.limit());
    }

    public BlogListResponse getBestBlogs(BlogBestQueryRequest query) {
        logger.info(
                "인기 블로그 목록 조회 - cursorId: {}, limit: {}", query.cursorId(), query.limit(), CONTEXT);
        List<Blog> blogs =
                blogRepository.findPopularBlogsWithCursor(query.cursorId(), query.limit());
        logger.info("인기 블로그 목록 조회 성공 - size: {}", blogs.size(), CONTEXT);
        return BlogMapper.toListResponse(blogs, query.limit());
    }

    public BlogResponse getBlog(Long blogId) {
        logger.info(String.format("블로그 ID %d 조회 요청", blogId), CONTEXT);
        Blog blog =
                blogRepository
                        .findByIdAndIsDeletedFalse(blogId)
                        .orElseThrow(
                                () -> {
                                    logger.warn(
                                            String.format("블로그 조회 실패 - 존재하지 않는 blogId: %d", blogId),
                                            CONTEXT);
                                    return new BlogNotFoundException();
                                });
        logger.info("단일 블로그 엔티티 목록 조회 성공", CONTEXT);
        return BlogMapper.toResponse(blog);
    }

    @Transactional
    public void increaseBlogViewCount(Long blogId) {
        logger.info(String.format("블로그 조회수 증가 처리 중 - blogId: %d", blogId), CONTEXT);
        Blog blog =
                blogRepository
                        .findByIdAndIsDeletedFalse(blogId)
                        .orElseThrow(
                                () -> {
                                    logger.warn(
                                            String.format(
                                                    "블로그 조회수 증가 실패 - 존재하지 않는 blogId: %d", blogId),
                                            CONTEXT);
                                    return new BlogNotFoundException();
                                });
        blog.increaseViewCount();
        logger.info(String.format("블로그 조회수 증가 성공 - viewCount: %d", blog.getViewCount()), CONTEXT);
    }

    @Transactional
    public BlogResponse deleteBlog(Long blogId, Long currentUserId) {
        logger.info(String.format("블로그 ID %d 삭제 요청", blogId), CONTEXT);
        Blog blog =
                blogRepository
                        .findByIdAndIsDeletedFalse(blogId)
                        .orElseThrow(
                                () -> {
                                    logger.warn(
                                            String.format(
                                                    "블로그 삭제 실패 - 존재하지 않거나 이미 삭제된 blogId: %d",
                                                    blogId),
                                            CONTEXT);
                                    return new BlogNotFoundException();
                                });

        // 권한 체크
        if (!blog.getUser().getId().equals(currentUserId)) {
            logger.warn(
                    String.format(
                            "블로그 삭제 실패 - 권한 없음 - blogId: %d, userId: %d", blogId, currentUserId),
                    CONTEXT);
            throw new BlogUnauthorizedException();
        }

        blog.softDelete();
        logger.info("블로그 삭제 성공", CONTEXT);
        eventPublisher.publishEvent(new IndexEvent.Delete("blog", blogId));
        logger.info("인덱스 삭제 요청 성공", CONTEXT);
        return BlogMapper.toResponse(blog);
    }

    public List<BlogUrlsResponse> getAllUserBlogUrl() {
        logger.info("모든 유저의 블로그 url 조회 처리 중", CONTEXT);
        List<BlogUrlsResponse> result =
                userRepository.findByIsDeletedFalse().stream()
                        .map(
                                user -> {
                                    List<String> urls = new ArrayList<>();
                                    if (user.getTistoryUrl() != null
                                            && !user.getTistoryUrl().trim().isEmpty()) {
                                        urls.add(user.getTistoryUrl());
                                    }
                                    if (user.getMediumUrl() != null
                                            && !user.getMediumUrl().trim().isEmpty()) {
                                        urls.add(user.getMediumUrl());
                                    }
                                    if (user.getVelogUrl() != null
                                            && !user.getVelogUrl().trim().isEmpty()) {
                                        urls.add(user.getVelogUrl());
                                    }
                                    return BlogMapper.toUrlsResponse(user, urls);
                                })
                        .toList();
        logger.info("모든 유저의 블로그 url 조회 성공", CONTEXT);
        return result;
    }

    @Transactional
    public void createBlog(CrawlingBlogResponse dto) {
        logger.info(
                "블로그 데이터 저장 시작 - userId: {}, posts: {}", dto.getUserId(), dto.getPosts(), CONTEXT);

        // 유효성 검사
        validateBlogRequest(dto);

        User author =
                userRepository
                        .findById(dto.getUserId())
                        .orElseThrow(
                                () -> {
                                    logger.warn(
                                            String.format(
                                                    "블로그 생성 실패 - 존재하지 않는 userId: %d",
                                                    dto.getUserId()),
                                            CONTEXT);
                                    return new BlogUserNotFoundException();
                                });

        logger.info("블로그 작성자 정보 조회 성공 - userId: {}", author.getId());

        List<BlogSaveRequest> failedPosts = new ArrayList<>();
        for (BlogSaveRequest post : dto.getPosts()) {
            try {
                saveBlogPost(post, author, dto.getCategory());
            } catch (BlogAlreadyExistsException e) {
                logger.warn(
                        "블로그 저장 실패 (중복 URL) - title: {}, url: {}",
                        post.getTitle(),
                        post.getUrl(),
                        CONTEXT);
                failedPosts.add(post);
            } catch (Exception e) {
                logger.error(
                        "블로그 저장 실패 - title: {}, url: {}, error: {}",
                        post.getTitle(),
                        post.getUrl(),
                        e.getMessage(),
                        e,
                        CONTEXT);
                failedPosts.add(post);
            }
        }

        if (!failedPosts.isEmpty()) {
            logger.warn(
                    "총 {}개 포스트 저장 실패 - userId: {}", failedPosts.size(), dto.getUserId(), CONTEXT);
            // 실패한 포스트들의 상세 정보 로깅
            failedPosts.forEach(
                    post ->
                            logger.warn(
                                    "실패한 포스트 - title: {}, url: {}",
                                    post.getTitle(),
                                    post.getUrl(),
                                    CONTEXT));
        }
    }

    private void validateBlogRequest(CrawlingBlogResponse dto) {
        if (dto == null
                || dto.getUserId() == null
                || dto.getPosts() == null
                || dto.getPosts().isEmpty()) {
            logger.warn("블로그 생성 요청 유효성 검사 실패 - dto: {}", dto, CONTEXT);
            throw new BlogInvalidRequestException();
        }
    }

    private void saveBlogPost(BlogSaveRequest post, User author, BlogCategory category) {
        try {
            logger.info("블로그 저장 시도 - title: {}, url: {}", post.getTitle(), post.getUrl(), CONTEXT);

            // URL 중복 체크
            if (blogRepository.existsByUrl(post.getUrl())) {
                logger.warn("블로그 저장 실패 - 중복된 URL: {}", post.getUrl(), CONTEXT);
                throw new BlogAlreadyExistsException();
            }

            LocalDateTime postDate = parsePostDate(post.getDate());
            Blog blog = createBlogEntity(post, author, category, postDate);
            Blog savedBlog = blogRepository.save(blog);
            logger.info(
                    "블로그 저장 성공 - id: {}, title: {}",
                    savedBlog.getId(),
                    savedBlog.getTitle(),
                    CONTEXT);
            eventPublisher.publishEvent(
                    new IndexEvent.Create<>("blog", BlogMapper.toIndexDto(savedBlog)));
        } catch (BlogAlreadyExistsException e) {
            throw e;
        } catch (Exception e) {
            logger.error(
                    "블로그 저장 실패 - title: {}, url: {}, error: {}",
                    post.getTitle(),
                    post.getUrl(),
                    e.getMessage(),
                    e,
                    CONTEXT);
            throw new BlogInvalidRequestException();
        }
    }

    private LocalDateTime parsePostDate(String date) {
        try {
            return LocalDateTime.parse(date, ISO_DATE_TIME);
        } catch (DateTimeParseException e) {
            logger.warn(
                    "날짜 파싱 실패, 현재 시간으로 대체 - date: {}, error: {}", date, e.getMessage(), CONTEXT);
            return LocalDateTime.now();
        }
    }

    private Blog createBlogEntity(
            BlogSaveRequest post, User author, BlogCategory category, LocalDateTime postDate) {
        return BlogMapper.toEntity(post, author, category, postDate);
    }
}
