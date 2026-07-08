package com.venturebridge.service;

import com.venturebridge.dto.StartupRequest;
import com.venturebridge.entity.Startup;
import com.venturebridge.entity.User;
import com.venturebridge.entity.VerificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StartupService {

    Startup createStartup(StartupRequest request, User founder);

    Startup updateStartup(Long startupId, StartupRequest request, User founder);

    void deleteStartup(Long startupId, User founder);

    Startup getStartupById(Long startupId);

    Startup getStartupByIdForFounder(Long startupId, User founder);

    Page<Startup> browseStartups(String searchTerm, Pageable pageable);

    Page<Startup> browseStartups(String searchTerm, String industry, String stage, java.math.BigDecimal minFunding, java.math.BigDecimal maxFunding, String location, Pageable pageable);

    List<Startup> getStartupsByFounder(User founder);

    List<Startup> findAllStartups();

    Startup verifyStartup(Long startupId);

    Startup markAsSpam(Long startupId);

    long countByFounder(User founder);
}