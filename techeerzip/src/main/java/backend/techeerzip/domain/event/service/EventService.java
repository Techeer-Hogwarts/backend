package backend.techeerzip.domain.event.service;

import backend.techeerzip.domain.event.entity.Event;
import backend.techeerzip.domain.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {
    private final EventRepository eventRepository;

    // TODO: 필요한 서비스 메서드 구현
} 