package com.gagreen.bowling.domain.score_record.roll;

import com.gagreen.bowling.domain.score_record.frame.FrameVo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "roll")
public class RollVo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "roll_id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "frame_id", nullable = false)
    private FrameVo frame;

    @Column(name = "roll_number")
    private Integer rollNumber;

    @Column(name = "pins")
    private Integer pins;
}