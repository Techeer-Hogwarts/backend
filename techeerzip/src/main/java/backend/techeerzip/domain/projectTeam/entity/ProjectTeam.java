package backend.techeerzip.domain.projectTeam.entity;

import backend.techeerzip.domain.projectMember.entity.ProjectMember;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectTeam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private Boolean isDeleted = false;

    @Column(nullable = false)
    private Boolean isRecruited = true;

    @Column(nullable = false)
    private Boolean isFinished = true;

    @Column(nullable = false, length = 100, unique = true)
    private String name;

    @Column(nullable = false, length = 500)
    private String githubLink;

    @Column(nullable = false, length = 500)
    private String notionLink;

    @Column(nullable = false, length = 500)
    private String projectExplain;

    @Column(nullable = false)
    private Integer frontendNum;

    @Column(nullable = false)
    private Integer backendNum;

    @Column(nullable = false)
    private Integer devopsNum;

    @Column(nullable = false)
    private Integer fullStackNum;

    @Column(nullable = false)
    private Integer dataEngineerNum;

    @Column(nullable = false, length = 3000)
    private String recruitExplain;

    @Column(nullable = false)
    private Integer likeCount = 0;

    @Column(nullable = false)
    private Integer viewCount = 0;

    @OneToMany(mappedBy = "projectTeam")
    private List<ProjectMember> projectMembers = new ArrayList<>();
} 