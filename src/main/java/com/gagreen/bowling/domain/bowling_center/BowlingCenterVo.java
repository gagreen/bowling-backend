package com.gagreen.bowling.domain.bowling_center;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
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
@Table(name = "bowling_center")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class BowlingCenterVo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "center_id", nullable = false)
    private Long id;

    @Size(max = 64)
    @Column(name = "name", length = 64)
    private String name;

    @Size(max = 32)
    @NotNull
    @Column(name = "state", nullable = false, length = 32)
    private String state;

    @Size(max = 32)
    @NotNull
    @Column(name = "city", nullable = false, length = 32)
    private String city;

    @Size(max = 32)
    @NotNull
    @Column(name = "district", nullable = false, length = 32)
    private String district;

    @Size(max = 32)
    @NotNull
    @Column(name = "detail_address", nullable = false, length = 32)
    private String detailAddress;


    @Size(max = 11)
    @Column(name = "tel_number", length = 11)
    private String telNumber;

    @Column(name = "lane_count")
    private Integer laneCount;

    @NotNull
    @ColumnDefault("(now())")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @ColumnDefault("(now())")
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;



    @Transient
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean isMyFavorite;

    @Transient
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String myNote;


}