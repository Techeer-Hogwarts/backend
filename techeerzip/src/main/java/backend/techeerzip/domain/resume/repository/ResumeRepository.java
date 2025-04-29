package backend.techeerzip.domain.resume.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import backend.techeerzip.domain.resume.entity.Resume;

public interface ResumeRepository extends JpaRepository<Resume, Long> {
    List<Resume> findByUserId(Long userId);
}
