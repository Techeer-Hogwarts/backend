package backend.techeerzip.domain.projectMember.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ProjectMemberDto {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Create {
        private Long userId;
        private Long projectTeamId;
        private String role;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private Long userId;
        private Long projectTeamId;
        private String role;
    }
}
