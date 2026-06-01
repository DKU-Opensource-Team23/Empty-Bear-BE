package com.dku.emptybear.domain.classroom.entity;

import com.dku.emptybear.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "classroom_view_history",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_classroom_view_history_user_classroom",
                columnNames = {"user_id", "classroom_id"}
        )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClassroomViewHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "classroom_view_history_id")
    private Long classroomViewHistoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classroom_id", nullable = false)
    private Classroom classroom;

    @Column(name = "viewed_at", nullable = false)
    private LocalDateTime viewedAt;

    private ClassroomViewHistory(User user, Classroom classroom) {
        this.user = user;
        this.classroom = classroom;
    }

    public static ClassroomViewHistory create(User user, Classroom classroom) {
        return new ClassroomViewHistory(user, classroom);
    }

    public void updateViewedAt() {
        this.viewedAt = LocalDateTime.now();
    }

    @PrePersist
    public void prePersist() {
        this.viewedAt = LocalDateTime.now();
    }
}
