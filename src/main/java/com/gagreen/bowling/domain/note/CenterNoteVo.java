package com.gagreen.bowling.domain.note;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gagreen.bowling.domain.bowling_center.BowlingCenterVo;
import com.gagreen.bowling.domain.user.UserVo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "center_note")
public class CenterNoteVo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "note_id", nullable = false)
    private Long id;

    @JsonIgnore
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserVo user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "center_id", nullable = false)
    private BowlingCenterVo center;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @NotNull
    @Builder.Default
    @ColumnDefault("(now())")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @NotNull
    @Builder.Default
    @ColumnDefault("(now())")
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

}