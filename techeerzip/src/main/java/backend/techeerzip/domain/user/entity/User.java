package backend.techeerzip.domain.user.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import backend.techeerzip.domain.blog.entity.Blog;
import backend.techeerzip.domain.bookmark.entity.Bookmark;
import backend.techeerzip.domain.event.entity.Event;
import backend.techeerzip.domain.like.entity.Like;
import backend.techeerzip.domain.projectMember.entity.ProjectMember;
import backend.techeerzip.domain.resume.entity.Resume;
import backend.techeerzip.domain.role.entity.Role;
import backend.techeerzip.domain.session.entity.Session;
import backend.techeerzip.domain.studyMember.entity.StudyMember;
import backend.techeerzip.domain.techBloggingChallenge.entity.TechBloggingAttendance;
import backend.techeerzip.domain.userExperience.entity.UserExperience;
import backend.techeerzip.global.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "User",
        uniqueConstraints = @UniqueConstraint(name = "User_email_key", columnNames = "email"))
public class User extends BaseEntity {

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private final List<Blog> blogs = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private final List<Bookmark> bookmarks = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private final List<Event> events = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private final List<Like> likes = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private final List<PermissionRequest> permissionRequests = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private final List<ProjectMember> projectMembers = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private final List<Resume> resumes = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private final List<Session> sessions = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private final List<StudyMember> studyMembers = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private final List<UserExperience> experiences = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<TechBloggingAttendance> techBloggingAttendances = new ArrayList<>();

    @Id
    @SequenceGenerator(name = "user_id_seq_gen", sequenceName = "User_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_seq_gen")
    private Long id;

    @Column(nullable = false)
    private boolean isDeleted;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(length = 200)
    private String nickname;

    @Column private Integer year;

    @Column(nullable = false)
    private String password;

    @Column private boolean isLft;

    @Column(length = 500)
    private String githubUrl;

    @Column(length = 100)
    private String mainPosition;

    @Column(length = 100)
    private String subPosition;

    @Column(length = 100)
    private String school;

    @Column(length = 1000)
    private String profileImage;

    @JdbcTypeCode(SqlTypes.ARRAY)
    private List<String> stack;

    @Column(nullable = false)
    private boolean isAuth;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roleId", nullable = false)
    private Role role;

    @Column(length = 100)
    private String grade;

    @Column(length = 300)
    private String mediumUrl;

    @Column(length = 300)
    private String tistoryUrl;

    @Column(length = 300)
    private String velogUrl;

    @Column(name = "bootcampYear")
    private Integer bootcampYear;

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
            List<String> stack,
            boolean isAuth,
            Role role,
            String grade,
            String mediumUrl,
            String tistoryUrl,
            String velogUrl,
            Integer bootcampYear) {
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
        this.bootcampYear = bootcampYear;
        this.isDeleted = false;
    }

    public void update(
            String name,
            String nickname,
            String githubUrl,
            String mainPosition,
            String subPosition,
            String school,
            String profileImage,
            List<String> stack,
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
    }

    public void delete() {
        this.isDeleted = true;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
