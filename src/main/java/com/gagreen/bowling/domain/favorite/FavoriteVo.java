package com.gagreen.bowling.domain.favorite;

import com.gagreen.bowling.domain.bowling_center.BowlingCenterVo;
import com.gagreen.bowling.domain.user.UserVo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "favorite_center")
public class FavoriteVo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserVo user;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "center_id", nullable = false)
    private BowlingCenterVo center;

    @NotNull
    @ColumnDefault("(now())")
    @Builder.Default
    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();


}