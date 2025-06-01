package backend.techeerzip.domain.blog.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import backend.techeerzip.domain.blog.entity.Blog;
import backend.techeerzip.domain.user.entity.User;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long>, BlogRepositoryCustom {
    Page<Blog> findByIsDeletedFalseAndCategoryOrderByCreatedAtDesc(
            String category, Pageable pageable);

    Page<Blog> findByIsDeletedFalseAndUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    @Query(
            "SELECT b FROM Blog b WHERE b.isDeleted = false AND b.createdAt >= :twoWeeksAgo ORDER BY b.likeCount DESC")
    Page<Blog> findBestBlogs(@Param("twoWeeksAgo") LocalDateTime twoWeeksAgo, Pageable pageable);

    Optional<Blog> findByIdAndIsDeletedFalse(Long id);

    boolean existsByUrl(String url);

    @Modifying
    @Query("UPDATE Blog b SET b.isDeleted = true WHERE b.user.id = :userId")
    void updateIsDeletedByUserId(@Param("userId") Long userId);

    int countByUserAndDateBetweenAndIsDeletedFalse(
            User user, LocalDateTime start, LocalDateTime end);

    List<Blog> findByUserAndDateBetweenAndIsDeletedFalse(
            User user, LocalDateTime start, LocalDateTime end);

    List<Blog> findByUserAndIsDeletedFalse(User user);
}
