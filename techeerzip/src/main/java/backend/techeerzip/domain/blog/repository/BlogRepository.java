package backend.techeerzip.domain.blog.repository;

import backend.techeerzip.domain.blog.entity.Blog;
import backend.techeerzip.domain.blog.entity.BlogCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface BlogRepository extends JpaRepository<Blog, Long> {
    Page<Blog> findByIsDeletedFalseAndCategoryOrderByCreatedAtDesc(String category, Pageable pageable);

    Page<Blog> findByIsDeletedFalseAndUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    @Query("SELECT b FROM Blog b WHERE b.isDeleted = false AND b.date >= :twoWeeksAgo ORDER BY b.viewCount DESC")
    Page<Blog> findBestBlogs(@Param("twoWeeksAgo") LocalDateTime twoWeeksAgo, Pageable pageable);
    
    @Query("SELECT b FROM Blog b WHERE b.isDeleted = false AND b.id = :blogId")
    Blog findByIdAndIsDeletedFalse(@Param("blogId") Long blogId);
}
