package backend.techeerzip.domain.blog.controller;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import backend.techeerzip.global.logger.CustomLogger;

@RestController
@RequestMapping("/api/v3/blogs")
public class BlogController {
    private final CustomLogger logger;

    @Autowired
    public BlogController(CustomLogger logger) {
        this.logger = logger;
    }

    @PostMapping("/test-error")
    public ResponseEntity<Map<String, String>> testErrorLogging(
            @RequestBody Map<String, String> body, HttpServletRequest request) {
        try {
            // 의도적으로 예외 발생
            throw new RuntimeException("테스트를 위한 예외 발생");
        } catch (Exception e) {
            // 상세한 에러 로깅
            logger.bodyError(e, "TEST_ERROR_001", "테스트 에러가 발생했습니다", 500, request);

            Map<String, String> response = new HashMap<>();
            response.put("error", "테스트 에러");
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
