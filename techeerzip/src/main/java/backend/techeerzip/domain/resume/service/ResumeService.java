package backend.techeerzip.domain.resume.service;

import backend.techeerzip.domain.resume.dto.response.ResumeCreateResponse;
import backend.techeerzip.domain.resume.dto.response.ResumeResponse;
import backend.techeerzip.domain.resume.entity.Resume;
import backend.techeerzip.domain.user.entity.User;
import backend.techeerzip.domain.user.repository.UserRepository;
import backend.techeerzip.global.logger.CustomLogger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.techeerzip.domain.resume.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ResumeService {
    private final CustomLogger logger;
    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;

    // 이력서 파일 이름을 반환
    // 이름 형식: "사용자이름(날짜)" 또는 "기본이름-사용자이름(날짜)"
    private String getResumeFileName(String userName, String baseName) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String date = now.format(formatter);

        String fileName;
        if (baseName == null || baseName.isEmpty()) {
            fileName = String.format("%s(%s)", userName, date);
        } else {
            fileName = String.format("%s-%s(%s)", baseName, userName, date);
        }
        return fileName;
    }

    // 특정 유저의 기존 메인 이력서를 전부 해제
    private void unsetMainResumeByUserId(Long userId) {
        logger.debug("기존 메인 이력서 해제 요청 처리 중 - UserID: {}", userId);

        resumeRepository.unsetMainResumeByUserId(userId);
    }

    @Transactional
    public ResumeCreateResponse createResume(Long userId, MultipartFile file, String title, String position, String category, Boolean isMain) {
        this.logger.debug("이력서 생성 요청 처리 중 - Title: {}, Position: {}, Category: {}, IsMain: {}", title, position, category, isMain);

        // TODO: googleDrive에 업로드 후 url 반환
        String driveUrl = "testURL";

        // 유저 정보 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        String fileName = getResumeFileName(user.getName(), null);

        final Resume resume = Resume.builder()
                .user(user)
                .title(fileName)
                .position(position)
                .url(driveUrl)
                .category(category)
                .isMain(isMain)
                .build();

        Resume createdResume = resumeRepository.save(resume);

        // TODO: 메인 이력서 중복 방지 처리

        // TODO: 인덱스 업데이트
        this.logger.debug("이력서 생성 완료 - ID: {}", createdResume.getId());

        return new ResumeCreateResponse(createdResume);

    }

    // 단일 이력서 조회
    @Transactional(readOnly = true)
    public ResumeResponse getResumeById(Long resumeId) {
        logger.debug("이력서 조회 요청 처리 중 - ID: {}", resumeId);

        // TODO: 커스텀 에러 처리
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("이력서를 찾을 수 없습니다."));

        logger.debug("이력서 조회 완료 - ID: {}", resume.getId());

        return new ResumeResponse(resume);
    }

    @Transactional
    public void deleteResumeById(Long userId, Long resumeId) {
        logger.debug("이력서 삭제 요청 처리 중 - UserID: {}, ResumeID: {}", userId, resumeId);

        Resume resume = resumeRepository.findByIdAndIsDeletedFalse(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("이력서를 찾을 수 없습니다."));

        // TODO: 권한처리 미들웨어로 분리 및 Forbidden에러 발생
        if(!resume.getUser().getId().equals(userId)) {
            logger.warn("이력서 삭제 권한 없음 - UserID: {}, ResumeID: {}", userId, resumeId);
            throw new IllegalArgumentException("이력서를 삭제할 권한이 없습니다.");
        }

        // TODO: 메인 이력서에서 해제해야 함
        resume.delete();

        // TODO: 인덱스 제거

        logger.debug("이력서 삭제 완료 - UserID: {}, ResumeID: {}", userId, resumeId);
    }

    @Transactional
    public void updateMainResume(Long resumeId, Long userId) {
        logger.debug("메인 이력서 업데이트 요청 처리 중 - ResumeID: {}, UserID: {}", resumeId, userId);
        Resume resume = resumeRepository.findByIdAndIsDeletedFalse(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("이력서를 찾을 수 없습니다."));

        // TODO: 권한처리 미들웨어로 분리 및 Forbidden에러 발생
        if(!resume.getUser().getId().equals(userId)) {
            logger.warn("메인 이력서 업데이트 권한 없음 - UserID: {}, ResumeID: {}", userId, resumeId);
            throw new IllegalArgumentException("이력서를 업데이트할 권한이 없습니다.");
        }

        // 기존 메인 이력서가 있다면 해제
        this.unsetMainResumeByUserId(userId);

        resume.setMain(true);
    }
}
