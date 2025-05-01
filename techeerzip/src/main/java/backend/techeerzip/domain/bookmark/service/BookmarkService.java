package backend.techeerzip.domain.bookmark.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.techeerzip.domain.bookmark.dto.BookmarkDto;
import backend.techeerzip.domain.bookmark.repository.BookmarkRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookmarkService {
    private final BookmarkRepository bookmarkRepository;

    public BookmarkDto.Response createBookmark(BookmarkDto.Create request) {
        // TODO: Implement bookmark creation logic
        return null;
    }
}
