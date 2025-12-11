package com.gagreen.bowling.domain.lane;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gagreen.bowling.domain.bowling_center.BowlingCenterVo;
import com.gagreen.bowling.domain.lane.code.LaneStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "lane")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class LaneVo {
    @Id
    @Column(name = "lane_id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "center_id", nullable = false)
    @JsonIgnore
    private BowlingCenterVo center;

    @Column(name = "lane_number")
    private Integer laneNumber;

    @Size(max = 255)
    @Column(name = "status")
    private String status;

    public void setStatus(LaneStatus status) {
        this.status = status.getCode();
    }

    @Transient
    public String getStatusDesc() {
        return LaneStatus.fromCode(this.status).getDescription();
    }

}