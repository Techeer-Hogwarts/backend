package backend.techeerzip.domain.blog.dto.request;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import backend.techeerzip.domain.blog.exception.BlogCrawlingException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BlogSaveRequest {
    private String title;
    private String url;
    private String author;
    private String authorImage;
    private String thumbnail;
    private String date;
    private List<String> tags;
    private String category;

    public BlogSaveRequest(Object post) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> postMap =
                    mapper.convertValue(post, new TypeReference<Map<String, Object>>() {});

            this.title = (String) postMap.get("title");
            this.url = (String) postMap.get("url");
            this.author = (String) postMap.get("author");
            this.authorImage = (String) postMap.get("authorImage");
            this.thumbnail = (String) postMap.get("thumbnail");
            this.date = (String) postMap.get("date");
            this.category = (String) postMap.get("category");

            // 타입 안전한 tags 처리
            Object tagsObj = postMap.get("tags");
            if (tagsObj == null) {
                this.tags = Collections.emptyList();
            } else if (tagsObj instanceof List<?>) {
                List<?> rawList = (List<?>) tagsObj;
                List<String> stringList = new ArrayList<>();
                for (Object item : rawList) {
                    if (item instanceof String) {
                        stringList.add((String) item);
                    }
                }
                this.tags = Collections.unmodifiableList(stringList);
            } else {
                this.tags = Collections.emptyList();
            }
        } catch (Exception e) {
            throw new BlogCrawlingException("블로그 포스트 데이터 변환 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
