package com.venturebridge.dto;

import com.venturebridge.entity.StartupStage;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Getter
@Setter
public class StartupRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotBlank
    private String industry;

    @NotBlank
    private String problemStatement;

    @NotBlank
    private String solution;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal fundRequired;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal equityOffered;

    @NotNull
    private StartupStage stage;

    @NotBlank
    private String location;

    private String businessModel;

    private String financialDetails;

    private MultipartFile logoFile;

    private MultipartFile pitchDeckFile;
}