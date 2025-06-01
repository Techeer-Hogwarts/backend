package backend.techeerzip.domain.projectTeam.dto.request;

import backend.techeerzip.domain.stack.entity.Stack;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class TeamStackInfo {

    private TeamStackInfo() {}

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WithName {

        @Schema(
                description = "스택 이름",
                example = "Spring Boot"
        )
        private String stack;

        @Schema(
                description = "대표 스택 여부 (true인 경우 프로젝트의 대표 기술 스택)",
                example = "true"
        )
        private Boolean isMain;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class WithStack {

        private Stack stack;
        private Boolean isMain;
    }
}
