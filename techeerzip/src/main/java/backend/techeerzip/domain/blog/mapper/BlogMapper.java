package backend.techeerzip.domain.blog.mapper;

import backend.techeerzip.domain.blog.dto.request.BlogIndexRequest;
import backend.techeerzip.domain.blog.entity.Blog;

public class BlogMapper {
    private BlogMapper() {
        throw new IllegalStateException("BlogMapper is a utility class");
    }

    public static BlogIndexRequest toIndexDto(Blog blog) {
        return BlogIndexRequest.builder()
                .id(String.valueOf(blog.getId()))
                .title(blog.getTitle())
                .url(blog.getUrl())
                .thumbnail(blog.getThumbnail())
                .date(blog.getDate().toLocalDate().toString())
                .userId(String.valueOf(blog.getUser().getId()))
                .userName(blog.getUser().getName())
                .userProfileImage(blog.getUser().getProfileImage())
                .stack(blog.getTags().toArray(new String[0]))
                .build();
    }
}
