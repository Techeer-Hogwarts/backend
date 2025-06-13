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
        private final String id;
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
        private final String id;
        private final String name;
        private final String studyExplain;
        private final List<String> resultImages;
        private final String title;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Stack {
        private final String id;
        private final String name;
        private final String category;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Resuem {
        private final String id;
        private final String title;
        private final String url;
        private final String createdAt;
        private final String userId;
        private final String userProfileImage;
        private final String year;
        private final String position;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class User {
        private final String id;
        private final String name;
        private final String school;
        private final String email;
        private final String year;
        private final String grade;
        private final List<String> stack;
        private final String profileImage;
    }
}
