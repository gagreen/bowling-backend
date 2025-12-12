package com.gagreen.bowling.domain.waiting;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gagreen.bowling.domain.bowling_center.BowlingCenterVo;
import com.gagreen.bowling.domain.user.UserVo;
import com.gagreen.bowling.domain.waiting.code.WaitingQueueStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "waiting_queue")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class WaitingQueueVo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "queue_id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private UserVo user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "center_id", nullable = false)
    @JsonIgnore
    private BowlingCenterVo center;

    @Column(name = "people_count")
    private Integer peopleCount;

    @Column(name = "order_no")
    private Integer orderNo;

    @Size(max = 255)
    @Column(name = "status")
    private String status;

    @NotNull
    @ColumnDefault("(now())")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public void setStatus(WaitingQueueStatus status) {
        this.status = status.getCode();
    }

    @Transient
    public String getStatusDesc() {
        return WaitingQueueStatus.fromCode(this.status).getDescription();
    }
}