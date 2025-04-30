package backend.techeerzip.domain.user.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
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
import backend.techeerzip.domain.userExperience.entity.UserExperience;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "\"User\"",
        uniqueConstraints = @UniqueConstraint(
                name = "\"User_email_key\"",
                columnNames = "email"
        )
)
public class User {

    @Id
    @SequenceGenerator(
            name = "user_id_seq_gen",
            sequenceName = "\"User_id_seq\"",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_seq_gen")
    private Integer id;

    @CreationTimestamp
    @Column(
            name = "createdAt",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(
            name = "updatedAt",
            nullable = false
    )
    private LocalDateTime updatedAt;

    @Column(
            name = "isDeleted",
            nullable = false
    )
    private boolean isDeleted;

    @Column(
            name = "name",
            nullable = false
    )
    private String name;

    @Column(
            name = "email",
            nullable = false
    )
    private String email;

    @Column(
            name = "nickname",
            length = 200
    )
    private String nickname;

    @Column(
            name = "year",
            nullable = false
    )
    private Integer year;

    @Column(
            name = "password",
            nullable = false
    )
    private String password;

    @Column(
            name = "isLft",
            nullable = false
    )
    private boolean isLft;

    @Column(
            name = "githubUrl",
            nullable = false,
            length = 500
    )
    private String githubUrl;

    @Column(
            name = "mainPosition",
            nullable = false,
            length = 100
    )
    private String mainPosition;

    @Column(
            name = "subPosition",
            length = 100
    )
    private String subPosition;

    @Column(
            name = "school",
            nullable = false,
            length = 100
    )
    private String school;

    @Column(
            name = "profileImage",
            nullable = false,
            length = 1000
    )
    private String profileImage;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(
            name = "stack"
    )
    private String[] stack;

    @Column(
            name = "isAuth",
            nullable = false
    )
    private boolean isAuth;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "roleId",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "\"User_roleId_fkey\""
            )
    )
    private Role role;

    @Column(
            name = "grade",
            nullable = false,
            length = 100
    )
    private String grade;

    @Column(
            name = "mediumUrl",
            length = 300
    )
    private String mediumUrl;

    @Column(
            name = "tistoryUrl",
            length = 300
    )
    private String tistoryUrl;

    @Column(
            name = "velogUrl",
            length = 300
    )
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
            String velogUrl
    ) {
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
        // createdAt/updatedAt/isDeleted default는 JPA/DB에 위임
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
            String velogUrl
    ) {
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
