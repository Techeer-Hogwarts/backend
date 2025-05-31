package backend.techeerzip.domain.projectTeam.dto.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
public class IndexRequest {

    private IndexRequest() {}

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Project {
        private final Long id;
        private final String name;
        private final String projectExplain;
        private final List<String> resultImages;
        private final List<String> teamStacks;
        private final String title;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Study {
        private final Long id;
        private final String name;
        private final String studyExplain;
        private final List<String> resultImages;
        private final String title;
    }
}
