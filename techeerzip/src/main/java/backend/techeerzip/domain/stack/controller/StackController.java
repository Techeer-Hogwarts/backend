package backend.techeerzip.domain.stack.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import backend.techeerzip.domain.stack.dto.StackDto;
import backend.techeerzip.domain.stack.service.StackService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v3/stacks")
@RequiredArgsConstructor
public class StackController {
    private final StackService stackService;

    @PostMapping
    public ResponseEntity<Void> createStack(@RequestBody StackDto.Create request) {
        stackService.create(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<StackDto.Response>> getStacksByCategory() {
        final List<StackDto.Response> stacks = stackService.getAll();
        return ResponseEntity.ok(stacks);
    }
}
