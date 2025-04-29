package backend.techeerzip.domain.user.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

import org.hibernate.annotations.Array;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import backend.techeerzip.domain.blog.entity.Blog;
import backend.techeerzip.domain.bookmark.entity.Bookmark;
import backend.techeerzip.domain.event.entity.Event;
import backend.techeerzip.domain.like.entity.Like;
import backend.techeerzip.domain.permissionRequest.entity.PermissionRequest;
import backend.techeerzip.domain.projectMember.entity.ProjectMember;
import backend.techeerzip.domain.resume.entity.Resume;
import backend.techeerzip.domain.session.entity.Session;
import backend.techeerzip.domain.studyMember.entity.StudyMember;
import backend.techeerzip.domain.userExperience.entity.UserExperience;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private boolean isDeleted;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(length = 200)
    private String nickname;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean isLft;

    @Column(nullable = false, length = 500)
    private String githubUrl;

    @Column(nullable = false, length = 100)
    private String mainPosition;

    @Column(length = 100)
    private String subPosition;

    @Column(nullable = false, length = 100)
    private String school;

    @Column(nullable = false, length = 1000)
    private String profileImage;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Array(length = 100)
    @Column(columnDefinition = "text[]")
    private String[] stack;

    @Column(nullable = false)
    private boolean isAuth;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(nullable = false, length = 100)
    private String grade;

    @Column(length = 300)
    private String mediumUrl;

    @Column(length = 300)
    private String tistoryUrl;

    @Column(length = 300)
    private String velogUrl;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Blog> blogs = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Bookmark> bookmarks = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Event> events = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Like> likes = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<PermissionRequest> permissionRequests = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<ProjectMember> projectMembers = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Resume> resumes = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Session> sessions = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<StudyMember> studyMembers = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserExperience> experiences = new ArrayList<>();

    @Builder
    public User(
            String name,
            String email,
            String nickname,
            Integer year,
            String password,
            boolean isLft,
            String githubUrl,
            String mainPosition,
            String subPosition,
            String school,
            String profileImage,
            String[] stack,
            boolean isAuth,
            Role role,
            String grade,
            String mediumUrl,
            String tistoryUrl,
            String velogUrl) {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isDeleted = false;
        this.name = name;
        this.email = email;
        this.nickname = nickname;
        this.year = year;
        this.password = password;
        this.isLft = isLft;
        this.githubUrl = githubUrl;
        this.mainPosition = mainPosition;
        this.subPosition = subPosition;
        this.school = school;
        this.profileImage = profileImage;
        this.stack = stack;
        this.isAuth = isAuth;
        this.role = role;
        this.grade = grade;
        this.mediumUrl = mediumUrl;
        this.tistoryUrl = tistoryUrl;
        this.velogUrl = velogUrl;
    }

    public void update(
            String name,
            String nickname,
            String githubUrl,
            String mainPosition,
            String subPosition,
            String school,
            String profileImage,
            String[] stack,
            String grade,
            String mediumUrl,
            String tistoryUrl,
            String velogUrl) {
        this.name = name;
        this.nickname = nickname;
        this.githubUrl = githubUrl;
        this.mainPosition = mainPosition;
        this.subPosition = subPosition;
        this.school = school;
        this.profileImage = profileImage;
        this.stack = stack;
        this.grade = grade;
        this.mediumUrl = mediumUrl;
        this.tistoryUrl = tistoryUrl;
        this.velogUrl = velogUrl;
        this.updatedAt = LocalDateTime.now();
    }

    public void delete() {
        this.isDeleted = true;
        this.updatedAt = LocalDateTime.now();
    }
}
