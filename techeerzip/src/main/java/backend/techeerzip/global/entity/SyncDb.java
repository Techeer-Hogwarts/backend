package backend.techeerzip.global.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "SyncDb")
public class SyncDb {

    @Id
    @SequenceGenerator(
            name = "syncdb_id_seq_gen",
            sequenceName = "SyncDb_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "syncdb_id_seq_gen")
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime lastSyncedAt;

    public void updateLastSyncedAt() {
        this.lastSyncedAt = LocalDateTime.now();
    }
}
