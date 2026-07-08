package com.venturebridge.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "investor_profiles")
public class InvestorProfile extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private String organization;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal investmentMin;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal investmentMax;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String experience;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String bio;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String industriesInterested;
}