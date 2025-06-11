package backend.techeerzip.domain.like.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.techeerzip.domain.blog.entity.Blog;
import backend.techeerzip.domain.blog.repository.BlogRepository;
import backend.techeerzip.domain.like.dto.request.LikeSaveRequest;
import backend.techeerzip.domain.like.dto.response.LikeListResponse;
import backend.techeerzip.domain.like.dto.response.LikedBlogResponse;
import backend.techeerzip.domain.like.dto.response.LikedContentResponse;
import backend.techeerzip.domain.like.dto.response.LikedResumeResponse;
import backend.techeerzip.domain.like.dto.response.LikedSessionResponse;
import backend.techeerzip.domain.like.dto.response.author.BlogAuthorResponse;
import backend.techeerzip.domain.like.dto.response.user.BlogUserResponse;
import backend.techeerzip.domain.like.dto.response.user.ResumeUserResponse;
import backend.techeerzip.domain.like.dto.response.user.SessionUserResponse;
import backend.techeerzip.domain.like.entity.Like;
import backend.techeerzip.domain.like.entity.LikeCategory;
import backend.techeerzip.domain.like.repository.LikeRepository;
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
public class LikeService {
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final BlogRepository blogRepository;
    private final SessionRepository sessionRepository;
    private final ResumeRepository resumeRepository;
    private final CustomLogger logger;
    private static final String CONTEXT = "LikeService";

    @Transactional
    public void createLike(Long userId, LikeSaveRequest request) {
        logger.info(
                "좋아요 생성/수정 요청 처리 중 - userId: {}, request: {} | context: {}",
                userId,
                request,
                CONTEXT);
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("User not found"));

        likeRepository
                .findByUserIdAndContentIdAndCategory(
                        userId, request.getContentId(), request.getCategory())
                .ifPresentOrElse(
                        like -> {
                            if (like.isDeleted()) {
                                like.reactivate(); // Reactivate soft-deleted like
                                updateContentLikeCount(
                                        request.getContentId(),
                                        LikeCategory.valueOf(request.getCategory()),
                                        true);
                                logger.info("삭제된 좋아요 재활성화 처리 완료 | context: {}", CONTEXT);
                            } else {
                                like.delete(); // Soft delete active like
                                updateContentLikeCount(
                                        request.getContentId(),
                                        LikeCategory.valueOf(request.getCategory()),
                                        false);
                                logger.info("기존 좋아요 삭제 처리 완료 | context: {}", CONTEXT);
                            }
                        },
                        () -> {
                            Like newLike =
                                    new Like(request.getContentId(), request.getCategory(), user);
                            likeRepository.save(newLike);
                            updateContentLikeCount(
                                    request.getContentId(),
                                    LikeCategory.valueOf(request.getCategory()),
                                    true);
                            logger.info("새로운 좋아요 생성 처리 완료 | context: {}", CONTEXT);
                        });

        logger.info("좋아요 생성/수정 요청 처리 완료 | context: {}", CONTEXT);
    }

    private void updateContentLikeCount(Long contentId, LikeCategory category, boolean isLiked) {
        switch (category) {
            case BLOG -> {
                Blog blog =
                        blogRepository
                                .findByIdAndIsDeletedFalse(contentId)
                                .orElseThrow(() -> new IllegalArgumentException("Blog not found"));
                if (isLiked) {
                    blog.increaseLikeCount();
                } else {
                    blog.decreaseLikeCount();
                }
                blogRepository.save(blog);
                logger.info(
                        "블로그 좋아요 카운트 업데이트 완료 - blogId: {}, isLiked: {} | context: {}",
                        contentId,
                        isLiked,
                        CONTEXT);
            }
            case SESSION -> {
                Session session =
                        sessionRepository
                                .findByIdAndIsDeletedFalse(contentId)
                                .orElseThrow(
                                        () -> new IllegalArgumentException("Session not found"));
                if (isLiked) {
                    session.increaseLikeCount();
                } else {
                    session.decreaseLikeCount();
                }
                sessionRepository.save(session);
                logger.info(
                        "세션 좋아요 카운트 업데이트 완료 - sessionId: {}, isLiked: {} | context: {}",
                        contentId,
                        isLiked,
                        CONTEXT);
            }
            case RESUME -> {
                Resume resume =
                        resumeRepository
                                .findByIdAndIsDeletedFalse(contentId)
                                .orElseThrow(
                                        () -> new IllegalArgumentException("Resume not found"));
                if (isLiked) {
                    resume.incrementLikeCount();
                } else {
                    resume.decrementLikeCount();
                }
                resumeRepository.save(resume);
                logger.info(
                        "이력서 좋아요 카운트 업데이트 완료 - resumeId: {}, isLiked: {} | context: {}",
                        contentId,
                        isLiked,
                        CONTEXT);
            }
        }
    }

    public LikeListResponse getLikeList(
            Long userId, LikeCategory category, Long cursorId, Integer limit) {
        logger.info(
                "좋아요 목록 조회 요청 처리 중 - userId: {}, category: {}, cursorId: {}, limit: {} | context: {}",
                userId,
                category,
                cursorId,
                limit,
                CONTEXT);

        // Get paginated likes using cursor-based pagination
        List<Like> likes =
                likeRepository.findLikesWithCursor(
                        userId, category.name(), cursorId == 0L ? null : cursorId, limit);

        // Transform likes to content responses
        List<LikedContentResponse> contents =
                likes.stream()
                        .map(
                                like -> {
                                    return switch (like.getCategory()) {
                                        case "BLOG" -> {
                                            Blog blog =
                                                    blogRepository
                                                            .findByIdAndIsDeletedFalse(
                                                                    like.getContentId())
                                                            .orElse(null);
                                            if (blog != null) {
                                                yield new LikedBlogResponse(
                                                        blog.getId(),
                                                        blog.getTitle(),
                                                        blog.getUrl(),
                                                        blog.getDate(),
                                                        blog.getCategory(),
                                                        blog.getCreatedAt(),
                                                        blog.getLikeCount(),
                                                        blog.getViewCount(),
                                                        blog.getThumbnail(),
                                                        new BlogAuthorResponse(
                                                                blog.getAuthor(),
                                                                blog.getAuthorImage()),
                                                        new BlogUserResponse(
                                                                blog.getUser().getId(),
                                                                blog.getUser().getName(),
                                                                blog.getUser().getNickname(),
                                                                blog.getUser().getRole().getId(),
                                                                blog.getUser().getProfileImage()));
                                            }
                                            yield null;
                                        }
                                        case "SESSION" -> {
                                            Session session =
                                                    sessionRepository
                                                            .findByIdAndIsDeletedFalse(
                                                                    like.getContentId())
                                                            .orElse(null);
                                            if (session != null) {
                                                yield new LikedSessionResponse(
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
                                                                session.getUser()
                                                                        .getProfileImage()));
                                            }
                                            yield null;
                                        }
                                        case "RESUME" -> {
                                            Resume resume =
                                                    resumeRepository
                                                            .findByIdAndIsDeletedFalse(
                                                                    like.getContentId())
                                                            .orElse(null);
                                            if (resume != null) {
                                                yield new LikedResumeResponse(
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
                                                                resume.getUser()
                                                                        .getRole()
                                                                        .getId()));
                                            }
                                            yield null;
                                        }
                                        default -> null;
                                    };
                                })
                        .filter(content -> content != null)
                        .collect(Collectors.toList());

        logger.info("좋아요 목록 조회 요청 처리 완료 | context: {}", CONTEXT);
        return new LikeListResponse(contents, limit);
    }
}
