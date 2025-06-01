package backend.techeerzip.domain.resume.dto.response;

import java.time.LocalDateTime;

import backend.techeerzip.domain.resume.entity.Resume;
import backend.techeerzip.domain.user.entity.User;
import lombok.Getter;

@Getter
public class ResumeCreateResponse {
    private final Long id;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final String title;
    private final String url;
    private final Boolean isMain;
    private final String category;
    private final String position;
    private final Integer likeCount;
    private final Integer viewCount;
    private final ResumeAuthorResponse user;

    public ResumeCreateResponse(Resume resume) {
        this.id = resume.getId();
        this.createdAt = resume.getCreatedAt();
        this.updatedAt = resume.getUpdatedAt();
        this.title = resume.getTitle();
        this.url = resume.getUrl();
        this.isMain = resume.isMain();
        this.category = resume.getCategory();
        this.position = resume.getPosition();
        this.likeCount = resume.getLikeCount();
        this.viewCount = resume.getViewCount();
        this.user = new ResumeAuthorResponse(resume.getUser());
    }

    @Getter
    public static class ResumeAuthorResponse {
        private final Long id;
        private final String name;
        private final String nickname;
        private final String profileImage;
        private final Integer year;
        private final String mainPosition;
        private final String subPosition;
        private final String school;
        private final String grade;
        private final String email;
        private final String githubUrl;
        private final String mediumUrl;
        private final String tistoryUrl;
        private final String velogUrl;
        private final Long roleId;

        public ResumeAuthorResponse(User user) {
            this.id = user.getId();
            this.name = user.getName();
            this.nickname = user.getNickname();
            this.profileImage = user.getProfileImage();
            this.year = user.getYear();
            this.mainPosition = user.getMainPosition();
            this.subPosition = user.getSubPosition();
            this.school = user.getSchool();
            this.grade = user.getGrade();
            this.email = user.getEmail();
            this.githubUrl = user.getGithubUrl();
            this.mediumUrl = user.getMediumUrl();
            this.tistoryUrl = user.getTistoryUrl();
            this.velogUrl = user.getVelogUrl();
            this.roleId = user.getRole().getId();
        }
    }
}
