package backend.techeerzip.domain.techBloggingChallenge.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import backend.techeerzip.domain.techBloggingChallenge.entity.TechBloggingTerm;
import backend.techeerzip.domain.techBloggingChallenge.entity.TermPeriod;

@Repository
public interface TechBloggingTermRepository extends JpaRepository<TechBloggingTerm, Long> {
    boolean existsByYearAndPeriodAndIsDeletedFalse(int year, TermPeriod period);

    Optional<TechBloggingTerm> findByYearAndPeriodAndIsDeletedFalse(int year, TermPeriod period);

    List<TechBloggingTerm> findByIsDeletedFalse();
}
