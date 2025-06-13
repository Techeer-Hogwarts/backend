package backend.techeerzip.domain.stack.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.techeerzip.domain.projectTeam.mapper.IndexMapper;
import backend.techeerzip.domain.stack.dto.StackDto;
import backend.techeerzip.domain.stack.dto.StackDto.Create;
import backend.techeerzip.domain.stack.entity.Stack;
import backend.techeerzip.domain.stack.entity.StackCategory;
import backend.techeerzip.domain.stack.repository.StackRepository;
import backend.techeerzip.infra.index.IndexEvent;
import backend.techeerzip.infra.index.IndexType;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StackService {
    private final StackRepository stackRepository;
    public final ApplicationEventPublisher eventPublisher;

    public List<StackDto.Response> getAll() {
        final List<Stack> stacks = stackRepository.findAllByIsDeletedFalse();
        return stacks.stream()
                .sorted(Comparator.comparing(Stack::getName))
                .map(
                        s ->
                                StackDto.Response.builder()
                                        .id(s.getId())
                                        .name(s.getName())
                                        .category(s.getCategory())
                                        .build())
                .toList();
    }

    @Transactional
    public void create(Create request) {
        final Stack stack =
                Stack.builder()
                        .name(request.getName())
                        .category(StackCategory.valueOf(request.getCategory()))
                        .build();
        final Stack saved = stackRepository.save(stack);
        eventPublisher.publishEvent(
                new IndexEvent.Create<>(
                        IndexType.STACK.getLow(), IndexMapper.toStackRequest(saved)));
    }
}
