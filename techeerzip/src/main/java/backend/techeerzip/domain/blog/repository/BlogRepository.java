package backend.techeerzip.domain.blog.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import backend.techeerzip.domain.blog.entity.Blog;

public interface BlogRepository extends JpaRepository<Blog, Long>, BlogRepositoryCustom {
    Page<Blog> findByIsDeletedFalseAndCategoryOrderByCreatedAtDesc(
            String category, Pageable pageable);

    Page<Blog> findByIsDeletedFalseAndUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    @Query(
            "SELECT b FROM Blog b WHERE b.isDeleted = false AND b.createdAt >= :twoWeeksAgo ORDER BY b.likeCount DESC")
    List<Blog> findBestBlogs(@Param("twoWeeksAgo") LocalDateTime twoWeeksAgo, Pageable pageable);

    Blog findByIdAndIsDeletedFalse(Long id);
}
