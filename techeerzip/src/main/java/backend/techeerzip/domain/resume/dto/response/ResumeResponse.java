package backend.techeerzip.domain.resume.dto.response;

import java.time.LocalDateTime;

import backend.techeerzip.domain.resume.entity.Resume;
import backend.techeerzip.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class ResumeResponse {
    private final Long id;
    private final String title;
    private final String url;
    private final Boolean isMain;
    private final String category;
    private final String position;
    private final Integer likeCount;
    private final Integer viewCount;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final UserInfo user;

    public ResumeResponse(Resume resume) {
        User resumeUser = resume.getUser();
        if (resumeUser == null) {
            throw new IllegalArgumentException("Resume must have a valid user");
        }
        this.id = resume.getId();
        this.title = resume.getTitle();
        this.url = resume.getUrl();
        this.isMain = resume.isMain();
        this.category = resume.getCategory();
        this.position = resume.getPosition();
        this.likeCount = resume.getLikeCount();
        this.viewCount = resume.getViewCount();
        this.createdAt = resume.getCreatedAt();
        this.updatedAt = resume.getUpdatedAt();
        this.user = UserInfo.from(resumeUser);
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private Long id;
        private String name;
        private String nickname;
        private String profileImage;
        private String email;
        private String githubUrl;
        private String mediumUrl;
        private String tistoryUrl;
        private String velogUrl;
        private String mainPosition;
        private String school;

        public static UserInfo from(User user) {
            return new UserInfo(
                    user.getId(),
                    user.getName(),
                    user.getNickname(),
                    user.getProfileImage(),
                    user.getEmail(),
                    user.getGithubUrl(),
                    user.getMediumUrl(),
                    user.getTistoryUrl(),
                    user.getVelogUrl(),
                    user.getMainPosition(),
                    user.getSchool()
            );
        }
    }
}
