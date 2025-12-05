package com.gagreen.bowling.domain.staff;

import com.gagreen.bowling.domain.bowling_center.BowlingCenterVo;
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
@Table(name = "staff")
public class StaffVo {
    @Id
    @Column(name = "staff_id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "center_id", nullable = false)
    private BowlingCenterVo center;

    @Size(max = 8)
    @Column(name = "name", length = 8)
    private String name;

    @Size(max = 11)
    @Column(name = "phone_number", length = 11)
    private String phoneNumber;

    @NotNull
    @ColumnDefault("(now())")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @ColumnDefault("(now())")
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

}