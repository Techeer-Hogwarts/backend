package backend.techeerzip.domain.like.dto.response;

import java.util.List;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LikeListResponse {
    private List<LikedContentResponse> contents;
}
