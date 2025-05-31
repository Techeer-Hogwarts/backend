package backend.techeerzip.domain.techBloggingChallenge.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import backend.techeerzip.domain.techBloggingChallenge.entity.TechBloggingTerm;

@Repository
public interface TechBloggingTermRepository extends JpaRepository<TechBloggingTerm, Long> {
    boolean existsByYearAndFirstHalfAndIsDeletedFalse(int year, boolean firstHalf);

    Optional<TechBloggingTerm> findByYearAndFirstHalfAndIsDeletedFalse(int year, boolean firstHalf);

    List<TechBloggingTerm> findByIsDeletedFalse();
}
