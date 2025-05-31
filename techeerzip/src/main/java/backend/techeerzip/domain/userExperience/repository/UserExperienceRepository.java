package backend.techeerzip.domain.userExperience.repository;

import backend.techeerzip.domain.userExperience.entity.UserExperience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserExperienceRepository extends JpaRepository<UserExperience, Long> {
    @Modifying
    @Query("UPDATE UserExperience ue SET ue.isDeleted = true WHERE ue.user.id = :userId")
    void updateIsDeletedByUserId(@Param("userId") Long userId);
}
