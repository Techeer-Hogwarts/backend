package backend.techeerzip.domain.blog.dto.request;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
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
        Map<String, Object> postMap = (Map<String, Object>) post;
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
    }
}
