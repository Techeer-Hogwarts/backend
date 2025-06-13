package backend.techeerzip.domain.resume.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import backend.techeerzip.domain.projectTeam.mapper.IndexMapper;
import backend.techeerzip.domain.resume.dto.response.ResumeCreateResponse;
import backend.techeerzip.domain.resume.dto.response.ResumeListResponse;
import backend.techeerzip.domain.resume.dto.response.ResumeResponse;
import backend.techeerzip.domain.resume.entity.Resume;
import backend.techeerzip.domain.resume.exception.ResumeInvalidTypeException;
import backend.techeerzip.domain.resume.exception.ResumeNotFoundException;
import backend.techeerzip.domain.resume.exception.ResumeUnAuthorizedException;
import backend.techeerzip.domain.resume.repository.ResumeRepository;
import backend.techeerzip.domain.user.entity.User;
import backend.techeerzip.domain.user.repository.UserRepository;
import backend.techeerzip.global.logger.CustomLogger;
import backend.techeerzip.infra.googleDrive.service.GoogleDriveService;
import backend.techeerzip.infra.index.IndexEvent;
import backend.techeerzip.infra.index.IndexType;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ResumeService {
    private final CustomLogger logger;
    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final GoogleDriveService googleDriveService;
    private final ApplicationEventPublisher eventPublisher;

    private static final String CONTEXT = "ResumeService";

    /** 이력서 파일 이름 변환 "baseName-userName(yyyy-MM-dd)" 형식으로 변환됩니다. */
    private String getResumeFileName(String userName, String baseName) {
        String dateTimeString = LocalDateTime.now().toString();

        String fileName;
        if (baseName == null || baseName.isEmpty()) {
            fileName = String.format("%s-%s", userName, dateTimeString);
        } else {
            // 사용자 이름 - ISO 8601 시간 문자열 - 기본 이름
            fileName = String.format("%s-%s-%s", userName, dateTimeString, baseName);
        }

        logger.debug(
                "이력서 파일 이름 변환 - 사용자 이름: {}, 기본 이름: {}, 변환된 파일 이름: {}",
                userName,
                baseName,
                fileName);
        return fileName;
    }

    // 특정 유저의 기존 메인 이력서를 전부 해제
    private void unsetMainResumeByUserId(Long userId) {
        logger.debug("기존 메인 이력서 해제 요청 처리 중 - UserID: {}", userId);

        resumeRepository.unsetMainResumeByUserId(userId);
    }

    @Transactional
    public ResumeCreateResponse createResume(
            Long userId,
            MultipartFile file,
            String title,
            String position,
            String category,
            Boolean isMain) {
        this.logger.info(
                "이력서 생성 요청 처리 중 - Title: {}, Position: {}, Category: {}, IsMain: {}",
                title,
                position,
                category,
                isMain);

        // 파일 타입 검사
        String mimeType = file.getContentType();
        if (mimeType == null || !mimeType.equalsIgnoreCase("application/pdf")) {
            logger.warn("잘못된 파일 형식 업로드 시도: {}", mimeType);
            throw new ResumeInvalidTypeException();
        }

        // 유저 정보 조회
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        String fileName = getResumeFileName(user.getName(), title);

        // Google Drive에 파일 업로드
        String driveUrl;
        try {
            driveUrl = googleDriveService.uploadFileBuffer(file.getBytes(), fileName);
        } catch (IOException e) {
            logger.error("Google Drive 파일 업로드 실패: {}", e.getMessage());
            throw new RuntimeException("Google Drive 파일 업로드 실패", e);
        }

        // 메인 이력서 중복 방지 처리
        if (isMain) {
            this.unsetMainResumeByUserId(userId);
        }

        final Resume resume =
                Resume.builder()
                        .user(user)
                        .title(fileName)
                        .position(position)
                        .url(driveUrl)
                        .category(category)
                        .isMain(isMain)
                        .build();

        Resume createdResume = resumeRepository.save(resume);

        eventPublisher.publishEvent(
                new IndexEvent.Create<>(
                        IndexType.RESUME.getLow(),
                        IndexMapper.toResumeRequest(createdResume, user)));
        this.logger.info(
                "이력서 생성 완료 - ID: {}, Title: {}, Position: {}, Category: {}, IsMain: {}",
                createdResume.getId(),
                createdResume.getTitle(),
                createdResume.getPosition(),
                createdResume.getCategory(),
                createdResume.isMain());

        return new ResumeCreateResponse(createdResume);
    }

    // 단일 이력서 조회
    @Transactional(readOnly = true)
    public ResumeResponse getResumeById(Long resumeId) {
        logger.info("이력서 조회 요청 처리 중 - ID: {}", resumeId);

        Resume resume =
                resumeRepository
                        .findByIdAndIsDeletedFalse(resumeId)
                        .orElseThrow(
                                () -> {
                                    logger.warn("이력서 조회 실패 - ID: {}", resumeId, CONTEXT);
                                    return new ResumeNotFoundException();
                                });

        logger.info("이력서 조회 완료 - ID: {}", resume.getId());

        return new ResumeResponse(resume);
    }

    @Transactional
    public void deleteResumeById(Long resumeId, Long userId) {
        logger.info("이력서 삭제 요청 처리 중 - UserID: {}, ResumeID: {}", userId, resumeId);

        Resume resume =
                resumeRepository
                        .findByIdAndIsDeletedFalse(resumeId)
                        .orElseThrow(
                                () -> {
                                    logger.warn("이력서 조회 실패 - ResumeID: {}", resumeId, CONTEXT);
                                    return new ResumeNotFoundException();
                                });

        if (!resume.getUser().getId().equals(userId)) {
            logger.warn("이력서 삭제 권한 없음 - UserID: {}, ResumeID: {}", userId, resumeId);
            throw new ResumeUnAuthorizedException();
        }

        resume.delete();

        eventPublisher.publishEvent(
                new IndexEvent.Delete(IndexType.RESUME.getLow(), resume.getId()));
        logger.info("이력서 삭제 완료 - UserID: {}, ResumeID: {}", userId, resumeId);
    }

    @Transactional
    public void updateMainResume(Long resumeId, Long userId) {
        logger.info("메인 이력서 업데이트 요청 처리 중 - ResumeID: {}, UserID: {}", resumeId, userId);
        Resume resume =
                resumeRepository
                        .findByIdAndIsDeletedFalse(resumeId)
                        .orElseThrow(
                                () -> {
                                    logger.warn("이력서 조회 실패 - ResumeID: {}", resumeId, CONTEXT);
                                    return new ResumeNotFoundException();
                                });

        if (!resume.getUser().getId().equals(userId)) {
            logger.warn("메인 이력서 업데이트 권한 없음 - UserID: {}, ResumeID: {}", userId, resumeId);
            throw new ResumeUnAuthorizedException();
        }

        // 기존 메인 이력서가 있다면 해제
        this.unsetMainResumeByUserId(userId);

        resume.setMain(true);
        logger.info("메인 이력서 업데이트 요청 처리 성공 - ResumeID: {}, UserID: {}", resumeId, userId);
    }

    @Transactional(readOnly = true)
    public ResumeListResponse getResumes(
            List<String> position,
            List<Integer> year,
            String category,
            Long cursorId,
            Integer limit) {
        logger.info(
                "이력서 목록 조회 요청 처리 중 - Position: {}, Year: {}, Category: {}, CursorId: {}, Limit: {}",
                position,
                year,
                category,
                cursorId,
                limit,
                CONTEXT);

        List<Resume> resumes =
                resumeRepository.findResumesWithCursor(position, year, category, cursorId, limit);

        List<ResumeResponse> responseList = resumes.stream().map(ResumeResponse::new).toList();

        logger.info("이력서 목록 조회 완료 - 개수: {}", responseList.size(), CONTEXT);
        return new ResumeListResponse(responseList, limit);
    }

    @Transactional(readOnly = true)
    public ResumeListResponse getBestResumes(Long cursorId, Integer limit) {
        logger.info("인기 이력서 목록 조회 요청 처리 중 - CursorId: {}, Limit: {}", cursorId, limit, CONTEXT);

        List<Resume> bestResumes = resumeRepository.findBestResumesWithCursor(cursorId, limit);
        List<ResumeResponse> responseList = bestResumes.stream().map(ResumeResponse::new).toList();

        logger.info("{}개의 인기 이력서 목록 조회 성공", responseList.size(), CONTEXT);
        return new ResumeListResponse(responseList, limit);
    }

    @Transactional(readOnly = true)
    public ResumeListResponse getUserResumes(Long userId, Long cursorId, Integer limit) {
        logger.info(
                "유저 이력서 목록 조회 요청 처리 중 - UserID: {}, CursorId: {}, Limit: {}",
                userId,
                cursorId,
                limit,
                CONTEXT);

        List<Resume> userResumes =
                resumeRepository.findUserResumesWithCursor(userId, cursorId, limit);
        List<ResumeResponse> responseList = userResumes.stream().map(ResumeResponse::new).toList();

        logger.info("{}개의 유저 이력서 목록 조회 성공", responseList.size());
        return new ResumeListResponse(responseList, limit);
    }
}
