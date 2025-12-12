package com.gagreen.bowling.domain.score_record.frame;

import com.gagreen.bowling.domain.score_record.game.GameVo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "frame")
public class FrameVo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "frame_id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "game_id", nullable = false)
    private GameVo game;

    @Column(name = "frame_number")
    private Integer frameNumber;

    @Column(name = "frame_score")
    private Integer frameScore;

    @Column(name = "is_completed")
    private Boolean isCompleted;

}