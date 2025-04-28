package backend.techeerzip.domain.bookmark.controller;

import backend.techeerzip.domain.bookmark.dto.BookmarkDto;
import backend.techeerzip.domain.bookmark.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {
    private final BookmarkService bookmarkService;

} 