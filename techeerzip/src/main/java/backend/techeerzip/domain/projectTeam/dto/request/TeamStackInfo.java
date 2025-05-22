package backend.techeerzip.domain.projectTeam.dto.request;

import backend.techeerzip.domain.stack.entity.Stack;
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

        private String stack;
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
