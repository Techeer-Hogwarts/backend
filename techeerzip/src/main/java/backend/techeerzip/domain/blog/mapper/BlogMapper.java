package backend.techeerzip.domain.blog.mapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import backend.techeerzip.domain.blog.dto.request.BlogIndexRequest;
import backend.techeerzip.domain.blog.dto.request.BlogSaveRequest;
import backend.techeerzip.domain.blog.dto.response.BlogAuthorResponse;
import backend.techeerzip.domain.blog.dto.response.BlogListResponse;
import backend.techeerzip.domain.blog.dto.response.BlogResponse;
import backend.techeerzip.domain.blog.dto.response.BlogUrlsResponse;
import backend.techeerzip.domain.blog.entity.Blog;
import backend.techeerzip.domain.blog.entity.BlogCategory;
import backend.techeerzip.domain.blog.exception.BlogInvalidRequestException;
import backend.techeerzip.domain.user.entity.User;
import backend.techeerzip.domain.blog.dto.response.CrawlingBlogResponse;
import backend.techeerzip.domain.blog.exception.BlogCrawlingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

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

    public static BlogResponse toResponse(Blog blog) {
        if (blog == null) {
            throw new BlogInvalidRequestException("Blog cannot be null");
        }

        // Author 정보 검증
        String authorName = blog.getAuthor();
        String authorImage = blog.getAuthorImage();
        if (authorName == null) {
            throw new BlogInvalidRequestException("Author name cannot be null");
        }
        if (authorImage == null) {
            throw new BlogInvalidRequestException("Author image cannot be null");
        }

        return BlogResponse.builder()
                .id(blog.getId())
                .title(blog.getTitle())
                .url(blog.getUrl())
                .date(blog.getDate())
                .category(blog.getCategory())
                .createdAt(blog.getCreatedAt())
                .likeCount(blog.getLikeCount())
                .viewCount(blog.getViewCount())
                .thumbnail(blog.getThumbnail())
                .author(new BlogResponse.Author(authorName, authorImage))
                .user(
                        blog.getUser() != null
                                ? BlogAuthorResponse.builder()
                                        .id(blog.getUser().getId())
                                        .name(blog.getUser().getName())
                                        .profileImage(blog.getUser().getProfileImage())
                                        .build()
                                : null)
                .build();
    }

    public static BlogListResponse toListResponse(List<Blog> blogs, int limit) {
        List<BlogResponse> blogResponses = blogs.stream().map(BlogMapper::toResponse).collect(Collectors.toList());

        boolean hasNext = blogResponses.size() > limit;
        Long nextCursor = hasNext ? blogResponses.get(limit - 1).getId() : null;

        return BlogListResponse.builder()
                .data(hasNext ? blogResponses.subList(0, limit) : blogResponses)
                .hasNext(hasNext)
                .nextCursor(nextCursor)
                .build();
    }

    public static BlogUrlsResponse toUrlsResponse(User user, List<String> blogUrls) {
        return BlogUrlsResponse.builder().userId(user.getId()).blogUrls(blogUrls).build();
    }

    public static BlogAuthorResponse toAuthorResponse(User user) {
        if (user == null) {
            return null;
        }
        return BlogAuthorResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .profileImage(user.getProfileImage())
                .build();
    }

    public static Blog toEntity(
            BlogSaveRequest post, User author, BlogCategory category, LocalDateTime postDate) {
        return Blog.builder()
                .user(author)
                .title(post.getTitle())
                .url(post.getUrl())
                .date(postDate)
                .author(post.getAuthor())
                .authorImage(post.getAuthorImage())
                .category(category != null ? category.name() : null)
                .thumbnail(post.getThumbnail())
                .tags(post.getTags())
                .build();
    }

    public static CrawlingBlogResponse fromTaskData(String taskData, BlogCategory category) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(taskData);

            // 필수 필드 검증
            JsonNode userIdNode = node.get("userId");
            JsonNode blogUrlNode = node.get("blogURL");

            if (userIdNode == null || userIdNode.isNull()) {
                throw new BlogCrawlingException("userId는 필수 필드입니다");
            }
            if (blogUrlNode == null || blogUrlNode.isNull()) {
                throw new BlogCrawlingException("blogURL은 필수 필드입니다");
            }

            return CrawlingBlogResponse.builder()
                    .userId(userIdNode.asLong())
                    .blogUrl(blogUrlNode.asText())
                    .posts(mapper.convertValue(node.get("posts"), new TypeReference<List<BlogSaveRequest>>() {
                    }))
                    .category(category)
                    .build();
        } catch (BlogCrawlingException e) {
            throw e;
        } catch (Exception e) {
            throw new BlogCrawlingException("크롤링 데이터 처리 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    public static CrawlingBlogResponse fromParams(Long userId, String blogUrl, List<BlogSaveRequest> posts,
            String category) {
        return CrawlingBlogResponse.builder()
                .userId(userId)
                .blogUrl(blogUrl)
                .posts(posts)
                .category(BlogCategory.valueOf(category))
                .build();
    }
}
