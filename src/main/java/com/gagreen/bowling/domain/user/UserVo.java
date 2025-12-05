package com.gagreen.bowling.domain.user;

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
@Table(name = "user")
public class UserVo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long id;

    @Size(max = 24)
    @NotNull
    @Column(name = "account", nullable = false, length = 24)
    private String account;

    @Size(max = 64)
    @NotNull
    @Column(name = "pw", nullable = false, length = 64)
    private String pw;

    @Size(max = 24)
    @NotNull
    @Column(name = "name", nullable = false, length = 24)
    private String name;

    @Size(max = 24)
    @NotNull
    @Column(name = "nickname", nullable = false, length = 24)
    private String nickname;

    @Size(max = 11)
    @NotNull
    @Column(name = "phone_number", nullable = false, length = 11)
    private String phoneNumber;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

}