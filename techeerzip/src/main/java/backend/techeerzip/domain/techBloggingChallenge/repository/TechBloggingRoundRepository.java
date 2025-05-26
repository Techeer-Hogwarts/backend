package backend.techeerzip.domain.techBloggingChallenge.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import backend.techeerzip.domain.techBloggingChallenge.entity.TechBloggingRound;

public interface TechBloggingRoundRepository extends JpaRepository<TechBloggingRound, Long> {
    // 필요시 커스텀 쿼리 추가
    List<TechBloggingRound> findByFirstHalfAndStartDateBetween(
            boolean firstHalf, LocalDate start, LocalDate end);

    boolean existsByFirstHalfAndIsDeletedFalseAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            boolean firstHalf, LocalDate end, LocalDate start);

    List<TechBloggingRound> findByIsDeletedFalse();
}
