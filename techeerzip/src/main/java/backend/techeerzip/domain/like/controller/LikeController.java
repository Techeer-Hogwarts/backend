package backend.techeerzip.domain.like.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import backend.techeerzip.domain.like.dto.LikeDto;
import backend.techeerzip.domain.like.service.LikeService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v3/likes")
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    @PostMapping
    public ResponseEntity<LikeDto.Response> createLike(@RequestBody LikeDto.Create request) {
        // TODO: Implement like creation
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{likeId}")
    public ResponseEntity<Void> deleteLike(@PathVariable Long likeId) {
        // TODO: Implement like deletion
        return ResponseEntity.ok().build();
    }
}
