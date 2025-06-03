package backend.techeerzip.domain.bookmark.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.techeerzip.domain.blog.entity.Blog;
import backend.techeerzip.domain.blog.repository.BlogRepository;
import backend.techeerzip.domain.bookmark.dto.request.BookmarkSaveRequest;
import backend.techeerzip.domain.bookmark.dto.response.BookmarkListResponse;
import backend.techeerzip.domain.bookmark.dto.response.BookmarkedBlogResponse;
import backend.techeerzip.domain.bookmark.dto.response.BookmarkedContentResponse;
import backend.techeerzip.domain.bookmark.dto.response.BookmarkedResumeResponse;
import backend.techeerzip.domain.bookmark.dto.response.BookmarkedSessionResponse;
import backend.techeerzip.domain.bookmark.entity.Bookmark;
import backend.techeerzip.domain.bookmark.entity.BookmarkCategory;
import backend.techeerzip.domain.bookmark.repository.BookmarkRepository;
import backend.techeerzip.domain.like.dto.response.author.BlogAuthorResponse;
import backend.techeerzip.domain.like.dto.response.user.BlogUserResponse;
import backend.techeerzip.domain.like.dto.response.user.ResumeUserResponse;
import backend.techeerzip.domain.like.dto.response.user.SessionUserResponse;
import backend.techeerzip.domain.resume.entity.Resume;
import backend.techeerzip.domain.resume.repository.ResumeRepository;
import backend.techeerzip.domain.session.entity.Session;
import backend.techeerzip.domain.session.repository.SessionRepository;
import backend.techeerzip.domain.user.entity.User;
import backend.techeerzip.domain.user.repository.UserRepository;
import backend.techeerzip.global.logger.CustomLogger;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookmarkService {
    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    private final BlogRepository blogRepository;
    private final SessionRepository sessionRepository;
    private final ResumeRepository resumeRepository;
    private final CustomLogger logger;
    private static final String CONTEXT = "BookmarkService";

    @Transactional
    public void createBookmark(Long userId, BookmarkSaveRequest request) {
        logger.info(
                "북마크 생성/수정 요청 처리 중 - userId: {}, request: {} | context: {}",
                userId,
                request,
                CONTEXT);
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("User not found"));

        bookmarkRepository
                .findByUserIdAndContentIdAndCategory(
                        userId, request.getContentId(), request.getCategory())
                .ifPresentOrElse(
                        bookmark -> {
                            if (bookmark.isDeleted()) {
                                bookmark.reactivate();
                                logger.info("삭제된 북마크 재활성화 처리 완료 | context: {}", CONTEXT);
                            } else {
                                bookmark.delete();
                                logger.info("기존 북마크 삭제 처리 완료 | context: {}", CONTEXT);
                            }
                        },
                        () -> {
                            Bookmark newBookmark =
                                    new Bookmark(
                                            request.getContentId(), request.getCategory(), user);
                            bookmarkRepository.save(newBookmark);
                            logger.info("새로운 북마크 생성 처리 완료 | context: {}", CONTEXT);
                        });

        logger.info("북마크 생성/수정 요청 처리 완료 | context: {}", CONTEXT);
    }

    public BookmarkListResponse getBookmarkList(
            Long userId, BookmarkCategory category, Long cursorId, Integer limit) {
        logger.info(
                "북마크 목록 조회 요청 처리 중 - userId: {}, category: {}, cursorId: {}, limit: {} | context: {}",
                userId,
                category,
                cursorId,
                limit,
                CONTEXT);

        List<Bookmark> bookmarks =
                bookmarkRepository.findBookmarksWithCursor(
                        userId, category.name(), cursorId == 0L ? null : cursorId, limit);

        List<BookmarkedContentResponse> contents =
                bookmarks.stream()
                        .map(bookmark -> {
                            String categoryNameString = bookmark.getCategory(); // 엔티티의 getCategory()가 String을 반환한다고 가정
                            BookmarkCategory categoryEnum;
                            try {
                                // DB에 저장된 문자열이 대소문자 구분 없이 Enum 상수와 일치하도록 toUpperCase() 사용
                                categoryEnum = BookmarkCategory.valueOf(categoryNameString.toUpperCase());
                            } catch (IllegalArgumentException e) {
                                logger.warn(
                                        "DB에 저장된 북마크 카테고리 문자열 '{}' (contentId: {})이(가) 유효한 Enum 값이 아닙니다. 이 북마크는 결과에서 제외됩니다. | context: {}",
                                        categoryNameString,
                                        bookmark.getContentId(),
                                        CONTEXT);
                                return null; // .filter(Objects::nonNull)에 의해 처리됨
                            }

                            return switch (categoryEnum) {
                                case BLOG -> createBookmarkedBlogResponse(bookmark.getContentId());
                                case SESSION -> createBookmarkedSessionResponse(bookmark.getContentId());
                                case RESUME -> createBookmarkedResumeResponse(bookmark.getContentId());
                                // BookmarkCategory Enum에 새로운 값이 추가되었으나, 여기에 매핑 로직이 없는 경우를 대비
                                // Java 14+ 필요
                            };
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

        logger.info("북마크 목록 조회 요청 처리 완료 | context: {}", CONTEXT);
        return new BookmarkListResponse(contents, limit);
    }

    private BookmarkedBlogResponse createBookmarkedBlogResponse(Long contentId) {
        Blog blog = blogRepository.findByIdAndIsDeletedFalse(contentId).orElse(null);
        if (blog != null) {
            return new BookmarkedBlogResponse(
                    blog.getId(),
                    blog.getTitle(),
                    blog.getUrl(),
                    blog.getDate(),
                    blog.getCategory(),
                    blog.getCreatedAt(),
                    blog.getLikeCount(),
                    blog.getViewCount(),
                    blog.getThumbnail(),
                    new BlogAuthorResponse(blog.getAuthor(), blog.getAuthorImage()),
                    new BlogUserResponse(
                            blog.getUser().getId(),
                            blog.getUser().getName(),
                            blog.getUser().getNickname(),
                            blog.getUser().getRole().getId(),
                            blog.getUser().getProfileImage()));
        }
        return null;
    }

    private BookmarkedSessionResponse createBookmarkedSessionResponse(Long contentId) {
        Session session = sessionRepository.findByIdAndIsDeletedFalse(contentId).orElse(null);
        if (session != null) {
            return new BookmarkedSessionResponse(
                    session.getId(),
                    session.getUser().getId(),
                    session.getThumbnail(),
                    session.getTitle(),
                    session.getPresenter(),
                    session.getDate(),
                    session.getPosition(),
                    session.getCategory(),
                    session.getVideoUrl(),
                    session.getFileUrl(),
                    session.getLikeCount(),
                    session.getViewCount(),
                    new SessionUserResponse(
                            session.getUser().getName(),
                            session.getUser().getNickname(),
                            session.getUser().getProfileImage()));
        }
        return null;
    }

    private BookmarkedResumeResponse createBookmarkedResumeResponse(Long contentId) {
        Resume resume = resumeRepository.findByIdAndIsDeletedFalse(contentId).orElse(null);
        if (resume != null) {
            return new BookmarkedResumeResponse(
                    resume.getId(),
                    resume.getCreatedAt(),
                    resume.getUpdatedAt(),
                    resume.getTitle(),
                    resume.getUrl(),
                    resume.isMain(),
                    resume.getCategory(),
                    resume.getPosition(),
                    resume.getLikeCount(),
                    resume.getViewCount(),
                    new ResumeUserResponse(
                            resume.getUser().getId(),
                            resume.getUser().getName(),
                            resume.getUser().getNickname(),
                            resume.getUser().getProfileImage(),
                            resume.getUser().getYear(),
                            resume.getUser().getMainPosition(),
                            resume.getUser().getSubPosition(),
                            resume.getUser().getSchool(),
                            resume.getUser().getGrade(),
                            resume.getUser().getEmail(),
                            resume.getUser().getGithubUrl(),
                            resume.getUser().getMediumUrl(),
                            resume.getUser().getTistoryUrl(),
                            resume.getUser().getVelogUrl(),
                            resume.getUser().getRole().getId()));
        }
        return null;
    }
}
