package com.venturebridge.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class InvestorProfileRequest {

    @NotBlank
    private String organization;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal investmentMin;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal investmentMax;

    @NotBlank
    private String experience;

    @NotBlank
    private String bio;

    private String industriesInterested;
}