package com.gagreen.bowling.domain.waiting;

import com.gagreen.bowling.domain.lane.LaneVo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "lane_assignment")
public class LaneAssignmentVo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "assign_id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lane_id", nullable = false)
    private LaneVo lane;

    @Column(name = "assigned_at")
    private Instant assignedAt;

    @Column(name = "finished_at")
    private Instant finishedAt;

}