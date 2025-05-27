package backend.techeerzip.domain.like.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "좋아요한 콘텐츠 응답")
public sealed interface LikedContentResponse permits 
    LikedBlogResponse, 
    LikedSessionResponse, 
    LikedResumeResponse {
    
    @Schema(description = "콘텐츠 ID")
    Long id();
} 