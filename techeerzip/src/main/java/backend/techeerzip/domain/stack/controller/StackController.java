package backend.techeerzip.domain.stack.controller;

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
    public ResponseEntity<StackDto.Response> createStack(@RequestBody StackDto.Create request) {
        // TODO: Implement stack creation
        return ResponseEntity.ok().build();
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<?> getStacksByCategory(@PathVariable String category) {
        // TODO: Implement getting stacks by category
        return ResponseEntity.ok().build();
    }
}
