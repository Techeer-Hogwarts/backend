package backend.techeerzip.domain.projectTeam.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ProjectTeamDto {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Create {
        private String name;
        private String description;
        private String startDate;
        private String endDate;
        private Integer maxMemberCount;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private String name;
        private String description;
        private String startDate;
        private String endDate;
        private Integer maxMemberCount;
    }
} 