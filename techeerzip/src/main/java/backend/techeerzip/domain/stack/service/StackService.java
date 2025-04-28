package backend.techeerzip.domain.stack.service;

import backend.techeerzip.domain.stack.entity.Stack;
import backend.techeerzip.domain.stack.repository.StackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StackService {
    private final StackRepository stackRepository;

    // TODO: 필요한 서비스 메서드 구현
} 