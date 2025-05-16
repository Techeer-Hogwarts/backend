package backend.techeerzip.domain.bookmark.controller;

import org.springframework.web.bind.annotation.*;

import backend.techeerzip.domain.bookmark.service.BookmarkService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v3/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {
    private final BookmarkService bookmarkService;
}
