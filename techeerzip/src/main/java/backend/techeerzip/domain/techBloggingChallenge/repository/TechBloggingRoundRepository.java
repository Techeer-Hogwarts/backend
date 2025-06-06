package backend.techeerzip.domain.techBloggingChallenge.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import backend.techeerzip.domain.techBloggingChallenge.entity.TechBloggingRound;
import backend.techeerzip.domain.techBloggingChallenge.entity.TechBloggingTerm;
import backend.techeerzip.domain.techBloggingChallenge.entity.TermPeriod;

@Repository
public interface TechBloggingRoundRepository extends JpaRepository<TechBloggingRound, Long> {
        List<TechBloggingRound> findByIsDeletedFalse();

        @Query("SELECT r FROM TechBloggingRound r WHERE r.term.period = :termPeriod AND r.dateRange.startDate BETWEEN :startDate AND :endDate AND r.isDeleted = false")
        List<TechBloggingRound> findByTermPeriodAndStartDateBetween(
                        @Param("termPeriod") TermPeriod termPeriod,
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM TechBloggingRound r "
                        + "WHERE r.term = :term AND r.isDeleted = false "
                        + "AND r.dateRange.startDate <= :endDate AND r.dateRange.endDate >= :startDate")
        boolean existsDuplicateRound(
                        @Param("term") TechBloggingTerm term,
                        @Param("endDate") LocalDate endDate,
                        @Param("startDate") LocalDate startDate);

        @Query("SELECT r FROM TechBloggingRound r WHERE r.dateRange.startDate <= :today AND r.dateRange.endDate >= :today AND r.isDeleted = false")
        List<TechBloggingRound> findActiveRoundsOnDate(@Param("today") LocalDate today);
}
