package backend.techeerzip.domain.blog.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.techeerzip.domain.blog.dto.request.BlogQueryRequest;
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

        public BlogListResponse getBlogList(BlogQueryRequest query) {
                logger.info("블로그 목록 조회 - cursorId: {}, category: {}, sortBy: {}, limit: {}",
                                query.getCursorId(), query.getCategory(), query.getSortBy(), query.getLimit());
                List<Blog> blogs = blogRepository.findBlogsWithCursor(
                                query.getCursorId(), query.getCategory(), query.getSortBy(), query.getLimit());
                List<BlogResponse> blogResponses = blogs.stream().map(BlogResponse::new).collect(Collectors.toList());
                logger.info("블로그 목록 조회 성공 - size: {}", blogResponses.size());
                return new BlogListResponse(blogResponses, query.getLimit());
        }

        public BlogListResponse getBlogsByUser(Long userId, Long cursorId, int limit) {
                logger.info("유저별 블로그 목록 조회 - userId: {}, cursorId: {}, limit: {}", userId, cursorId, limit);
                List<Blog> blogs = blogRepository.findUserBlogsWithCursor(userId, cursorId, limit);
                List<BlogResponse> blogResponses = blogs.stream().map(BlogResponse::new).collect(Collectors.toList());
                logger.info("유저별 블로그 목록 조회 성공 - size: {}", blogResponses.size());
                return new BlogListResponse(blogResponses, limit);
        }

        public BlogListResponse getBestBlogs(Long cursorId, int limit) {
                logger.info("인기 블로그 목록 조회 - cursorId: {}, limit: {}", cursorId, limit);
                List<Blog> blogs = blogRepository.findPopularBlogsWithCursor(cursorId, limit);
                List<BlogResponse> blogResponses = blogs.stream().map(BlogResponse::new).collect(Collectors.toList());
                logger.info("인기 블로그 목록 조회 성공 - size: {}", blogResponses.size());
                return new BlogListResponse(blogResponses, limit);
        }

        public BlogResponse getBlog(Long blogId) {
                logger.info(String.format("블로그 ID %d 조회 요청", blogId), CONTEXT);
                Blog blog = blogRepository.findByIdAndIsDeletedFalse(blogId)
                                .orElseThrow(() -> {
                                        logger.warn(String.format("블로그 조회 실패 - 존재하지 않는 blogId: %d", blogId), CONTEXT);
                                        return new BlogNotFoundException();
                                });
                logger.info("단일 블로그 엔티티 목록 조회 성공", CONTEXT);
                return new BlogResponse(blog);
        }

        @Transactional
        public void increaseBlogViewCount(Long blogId) {
                logger.info(String.format("블로그 조회수 증가 처리 중 - blogId: %d", blogId), CONTEXT);
                Blog blog = blogRepository.findByIdAndIsDeletedFalse(blogId)
                                .orElseThrow(() -> {
                                        logger.warn(String.format("블로그 조회수 증가 실패 - 존재하지 않는 blogId: %d", blogId),
                                                        CONTEXT);
                                        return new BlogNotFoundException();
                                });
                blog.increaseViewCount();
                logger.info(String.format("블로그 조회수 증가 성공 - viewCount: %d", blog.getViewCount()), CONTEXT);
        }

        @Transactional
        public BlogResponse deleteBlog(Long blogId, Long currentUserId) {
                logger.info(String.format("블로그 ID %d 삭제 요청", blogId), CONTEXT);
                Blog blog = blogRepository.findByIdAndIsDeletedFalse(blogId)
                                .orElseThrow(() -> {
                                        logger.warn(String.format("블로그 삭제 실패 - 존재하지 않거나 이미 삭제된 blogId: %d", blogId),
                                                        CONTEXT);
                                        return new BlogNotFoundException();
                                });

                // 권한 체크
                if (!blog.getUser().getId().equals(currentUserId)) {
                        logger.warn(String.format("블로그 삭제 실패 - 권한 없음 - blogId: %d, userId: %d", blogId, currentUserId),
                                        CONTEXT);
                        throw new BlogUnauthorizedException();
                }

                blog.softDelete();
                logger.info("블로그 삭제 성공", CONTEXT);
                eventPublisher.publishEvent(new IndexEvent.Delete("blog", blogId));
                return new BlogResponse(blog);
        }

        public List<BlogUrlsResponse> getAllUserBlogUrl() {
                logger.info("모든 유저의 블로그 url 조회 처리 중", CONTEXT);
                List<BlogUrlsResponse> result = userRepository.findByIsDeletedFalse().stream()
                                .map(user -> {
                                        List<String> validUrls = List.of(
                                                        user.getTistoryUrl(),
                                                        user.getMediumUrl(),
                                                        user.getVelogUrl()).stream()
                                                        .filter(url -> url != null && !url.trim().isEmpty())
                                                        .collect(Collectors.toList());

                                        return new BlogUrlsResponse(user.getId(), validUrls);
                                })
                                .collect(Collectors.toList());
                logger.info("모든 유저의 블로그 url 조회 성공", CONTEXT);
                return result;
        }

        @Transactional
        public void createBlog(CrawlingBlogResponse dto) {
                logger.info("블로그 데이터 저장 시작 - userId: {}, posts: {}", dto.getUserId(), dto.getPosts());

                // 유효성 검사
                validateBlogRequest(dto);

                User author = userRepository.findById(dto.getUserId())
                                .orElseThrow(() -> {
                                        logger.warn(String.format("블로그 생성 실패 - 존재하지 않는 userId: %d", dto.getUserId()),
                                                        CONTEXT);
                                        return new EntityNotFoundException("User not found: " + dto.getUserId());
                                });

                logger.info("블로그 작성자 정보 조회 성공 - userId: {}", author.getId());
                dto.getPosts().forEach(post -> saveBlogPost(post, author, dto.getCategory()));
        }

        private void validateBlogRequest(CrawlingBlogResponse dto) {
                if (dto == null || dto.getUserId() == null || dto.getPosts() == null || dto.getPosts().isEmpty()) {
                        logger.warn("블로그 생성 요청 유효성 검사 실패 - dto: {}", dto);
                        throw new BlogInvalidRequestException();
                }
        }

        private void saveBlogPost(BlogSaveRequest post, User author, BlogCategory category) {
                try {
                        logger.info("블로그 저장 시도 - title: {}, url: {}", post.getTitle(), post.getUrl());

                        // URL 중복 체크
                        if (blogRepository.existsByUrl(post.getUrl())) {
                                logger.warn("블로그 저장 실패 - 중복된 URL: {}", post.getUrl());
                                throw new BlogAlreadyExistsException();
                        }

                        LocalDateTime postDate = parsePostDate(post.getDate());
                        Blog blog = createBlogEntity(post, author, category, postDate);
                        blogRepository.save(blog);
                        logger.info("블로그 저장 성공 - id: {}, title: {}", blog.getId(), blog.getTitle());
                        eventPublisher.publishEvent(new IndexEvent.Create<>("blog", blog));
                } catch (BlogAlreadyExistsException e) {
                        throw e;
                } catch (Exception e) {
                        logger.error("블로그 저장 실패 - title: {}, url: {}, error: {}",
                                        post.getTitle(), post.getUrl(), e.getMessage(), e);
                        throw new BlogInvalidRequestException();
                }
        }

        private LocalDateTime parsePostDate(String date) {
                try {
                        return LocalDateTime.parse(date, ISO_DATE_TIME);
                } catch (DateTimeParseException e) {
                        logger.warn("날짜 파싱 실패, 현재 시간으로 대체 - date: {}, error: {}", date, e.getMessage());
                        return LocalDateTime.now();
                }
        }

        private Blog createBlogEntity(BlogSaveRequest post, User author, BlogCategory category,
                        LocalDateTime postDate) {
                return Blog.builder()
                                .user(author)
                                .title(post.getTitle())
                                .url(post.getUrl())
                                .date(postDate)
                                .author(post.getAuthor())
                                .authorImage(post.getAuthorImage())
                                .category(category != null ? category.name() : null)
                                .thumbnail(post.getThumbnail())
                                .tags(post.getTags())
                                .build();
        }
}
