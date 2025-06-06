package backend.techeerzip.domain.resume.repository;

import java.util.List;

import backend.techeerzip.domain.resume.entity.Resume;

public interface ResumeRepositoryCustom {
    /**
     * 필터를 적용하여 이력서 목록을 커서 기반으로 조회
     *
     * @param position 포지션 (복수 선택 가능)
     * @param year 기수 (복수 선택 가능)
     * @param category 카테고리
     * @param cursorId 마지막으로 조회한 이력서 ID
     * @param limit 조회할 개수
     * @return 이력서 목록
     */
    List<Resume> findResumesWithCursor(
            List<String> position,
            List<Integer> year,
            String category,
            Long cursorId,
            Integer limit);

    /**
     * 특정 사용자의 이력서 목록을 커서 기반 페이지네이션으로 조회
     *
     * @param userId 유저 ID
     * @param cursorId 커서 ID(이전 페이지의 마지막 이력서 ID)
     * @param limit 가져올 개수
     * @return 이력서 목록
     */
    List<Resume> findUserResumesWithCursor(Long userId, Long cursorId, Integer limit);

    /**
     * 인기 이력서 목록을 커서 기반 페이지네이션으로 조회
     *
     * @param cursorId 커서 ID(이전 페이지의 마지막 이력서 ID)
     * @param limit 가져올 개수
     * @return 이력서 목록
     */
    List<Resume> findBestResumesWithCursor(Long cursorId, Integer limit);
}
