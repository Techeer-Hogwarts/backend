package backend.techeerzip.domain.user.repository;

import java.util.List;

import backend.techeerzip.domain.user.entity.User;

public interface UserRepositoryCustom {
    List<User> findUsersWithCursor(
            Long cursorId,
            List<String> positions,
            List<Integer> years,
            List<String> universities,
            List<String> grades,
            int limit,
            String sortBy);
}
