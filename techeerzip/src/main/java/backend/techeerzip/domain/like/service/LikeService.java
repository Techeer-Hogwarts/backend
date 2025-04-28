package backend.techeerzip.domain.like.service;

import backend.techeerzip.domain.like.entity.Like;
import backend.techeerzip.domain.like.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeService {
    private final LikeRepository likeRepository;

    // TODO: 필요한 서비스 메서드 구현
} 