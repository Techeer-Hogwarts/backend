package backend.techeerzip.domain.userExperience.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import backend.techeerzip.domain.userExperience.entity.UserExperience;

public interface UserExperienceRepository extends JpaRepository<UserExperience, Long> {
    @Modifying
    @Query("DELETE UserExperience ue WHERE ue.user.id = :userId")
    void deletedByUserId(@Param("userId") Long userId);
}
