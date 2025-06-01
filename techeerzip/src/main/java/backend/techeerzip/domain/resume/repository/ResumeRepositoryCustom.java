package backend.techeerzip.domain.resume.repository;

import java.util.List;
import java.time.LocalDateTime;
import backend.techeerzip.domain.resume.entity.Resume;

public interface ResumeRepositoryCustom {
    /**
     * 필터를 적용하여 이력서 목록을 조회
     * @param position 포지션 (복수 선택 가능)
     * @param year 기수 (복수 선택 가능)
     * @param category 카테고리
     * @param offset 오프셋
     * @param limit 개수
     * @return 이력서 목록
     */
    List<Resume> findResumesWithFilter(
            List<String> position,
            List<Integer> year,
            String category,
            Integer offset,
            Integer limit
    );

    /**
     * 최근 2주 이내 생성된(isDeleted=false) 이력서 목록을 조회
     * @param createdAt 기준일(2주 전)
     * @return 이력서 목록
     */
    List<Resume> findBestResumes(LocalDateTime createdAt);
}