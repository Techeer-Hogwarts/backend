package backend.techeerzip.domain.user.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.techeerzip.domain.user.entity.User;
import backend.techeerzip.domain.user.exception.UserNotFoundException;
import backend.techeerzip.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;

    public <T> Map<Long, User> getIdAndUserMap(List<T> usersInfo, Function<T, Long> idExtractor) {
        final List<Long> usersId = usersInfo.stream().map(idExtractor).toList();
        final List<User> users = userRepository.findAllById(usersId);
        final Map<Long, User> userMap =
                users.stream().collect(Collectors.toMap(User::getId, user -> user));
        for (Long id : usersId) {
            if (!userMap.containsKey(id)) {
                throw new UserNotFoundException();
            }
        }
        return userMap;
    }
}
