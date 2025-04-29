package backend.techeerzip.domain.user.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", insertable = false, updatable = false)
    private Role parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Role> children = new ArrayList<>();

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
    private List<User> users = new ArrayList<>();

    @Builder
    public Role(String name, Long parentId) {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.name = name;
        this.parentId = parentId;
    }

    public void update(String name, Long parentId) {
        this.name = name;
        this.parentId = parentId;
        this.updatedAt = LocalDateTime.now();
    }

    public void addChild(Role child) {
        this.children.add(child);
        child.setParent(this);
    }

    private void setParent(Role parent) {
        this.parent = parent;
        this.parentId = parent.getId();
    }
}
