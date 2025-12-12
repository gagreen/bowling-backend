package com.gagreen.bowling.domain.staff;

import com.gagreen.bowling.domain.bowling_center.BowlingCenterVo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "staff")
public class StaffVo implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "staff_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "center_id")
    private BowlingCenterVo center;

    @Size(max = 64)
    @NotNull
    @Column(name = "account", nullable = false, length = 64)
    private String account;

    @JsonIgnore
    @Size(max = 64)
    @NotNull
    @Column(name = "pw", nullable = false, length = 64)
    private String pw;

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

    @JsonIgnore
    @Size(max = 255)
    @Column(name = "refresh_token", length = 255)
    private String refreshToken;

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("STAFF"));
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return this.pw;
    }

    @JsonIgnore
    @Override
    public String getUsername() {
        return this.account;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }
}