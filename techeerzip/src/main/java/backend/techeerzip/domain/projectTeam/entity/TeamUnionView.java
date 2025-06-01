package backend.techeerzip.domain.projectTeam.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.Immutable;

import backend.techeerzip.domain.projectTeam.type.TeamType;
import backend.techeerzip.global.entity.BaseEntity;
import lombok.Getter;

@Entity
@Getter
@Immutable
@Table(name = "TeamUnionView")
public class TeamUnionView extends BaseEntity {

    @Id
    @Column(name = "globalId")
    private UUID globalId;

    @Column private Long id;

    private Integer viewCount;
    private Integer likeCount;

    private Boolean isDeleted;
    private Boolean isRecruited;
    private Boolean isFinished;

    @Enumerated(EnumType.STRING)
    private TeamType teamType;
}
