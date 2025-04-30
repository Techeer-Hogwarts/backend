package backend.techeerzip.domain.role.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "Role",
        uniqueConstraints = @UniqueConstraint(name = "Role_name_key", columnNames = "name"))
public class Role {
    @Id
    @SequenceGenerator(name = "role_id_seq_gen", sequenceName = "Role_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_id_seq_gen")
    private Integer id;

    @Column(nullable = false, columnDefinition = "text")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parentId", foreignKey = @ForeignKey(name = "Role_parentId_fkey"))
    private Role parent;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public Role(String name) {
        this.name = name;
    }

    public Role(String name, Role parent) {
        this.name = name;
        this.parent = parent;
    }

    public void setParent(Role parent) {
        this.parent = parent;
    }
}
