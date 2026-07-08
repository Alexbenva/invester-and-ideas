package com.venturebridge.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
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
@Table(name = "startups")
public class Startup extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String industry;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String problemStatement;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String solution;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal fundRequired;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal equityOffered;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StartupStage stage;

    private String location;

    private String logo;

    private String pitchDeck;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String businessModel;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String financialDetails;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;

    @Column(nullable = false)
    private boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "founder_id", nullable = false)
    private User founder;
}