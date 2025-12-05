package com.gagreen.bowling.domain.score_record.lane;

import com.gagreen.bowling.domain.bowling_center.BowlingCenterVo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "lane")
public class LaneVo {
    @Id
    @Column(name = "lane_id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "center_id", nullable = false)
    private BowlingCenterVo center;

    @Column(name = "lane_number")
    private Integer laneNumber;

    @Size(max = 255)
    @Column(name = "status")
    private String status;

}