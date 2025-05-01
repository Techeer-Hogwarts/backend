package backend.techeerzip.domain.studyMember.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.techeerzip.domain.studyMember.repository.StudyMemberRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyMemberService {
    private final StudyMemberRepository studyMemberRepository;

    // TODO: 필요한 서비스 메서드 구현
}
