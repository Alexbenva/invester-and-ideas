package com.venturebridge.service.impl;

import com.venturebridge.dto.StartupRequest;
import com.venturebridge.entity.Startup;
import com.venturebridge.entity.User;
import com.venturebridge.entity.VerificationStatus;
import com.venturebridge.exception.ResourceNotFoundException;
import com.venturebridge.repository.StartupRepository;
import com.venturebridge.service.FileStorageService;
import com.venturebridge.service.StartupService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StartupServiceImpl implements StartupService {

    private final StartupRepository startupRepository;
    private final FileStorageService fileStorageService;

    @Override
    public Startup createStartup(StartupRequest request, User founder) {
        Startup startup = new Startup();
        mapRequest(startup, request);
        startup.setFounder(founder);
        startup.setVerificationStatus(VerificationStatus.PENDING);
        startup.setActive(true);
        return startupRepository.save(startup);
    }

    @Override
    public Startup updateStartup(Long startupId, StartupRequest request, User founder) {
        Startup startup = getStartupByIdForFounder(startupId, founder);
        mapRequest(startup, request);
        return startupRepository.save(startup);
    }

    @Override
    public void deleteStartup(Long startupId, User founder) {
        Startup startup = getStartupByIdForFounder(startupId, founder);
        startup.setActive(false);
        startupRepository.save(startup);
    }

    @Override
    @Transactional(readOnly = true)
    public Startup getStartupById(Long startupId) {
        return startupRepository.findByIdWithFounder(startupId)
                .orElseThrow(() -> new ResourceNotFoundException("Startup not found: " + startupId));
    }

    @Override
    @Transactional(readOnly = true)
    public Startup getStartupByIdForFounder(Long startupId, User founder) {
        return startupRepository.findByIdAndFounder(startupId, founder)
                .orElseThrow(() -> new ResourceNotFoundException("Startup not found for founder"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Startup> browseStartups(String searchTerm, Pageable pageable) {
        if (searchTerm == null || searchTerm.isBlank()) {
            return startupRepository.findByActiveTrue(pageable);
        }
        return startupRepository.findByActiveTrueAndTitleContainingIgnoreCase(searchTerm.trim(), pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Startup> browseStartups(String searchTerm, String industry, String stage, java.math.BigDecimal minFunding, java.math.BigDecimal maxFunding, String location, Pageable pageable) {
        com.venturebridge.entity.StartupStage stageEnum = null;
        if (stage != null && !stage.isBlank()) {
            try {
                stageEnum = com.venturebridge.entity.StartupStage.valueOf(stage.toUpperCase().trim());
            } catch (Exception exception) {
                // Ignore invalid stage
            }
        }
        return startupRepository.filterStartups(
            (searchTerm != null && !searchTerm.isBlank()) ? searchTerm.trim() : null,
            (industry != null && !industry.isBlank()) ? industry.trim() : null,
            stageEnum,
            minFunding,
            maxFunding,
            (location != null && !location.isBlank()) ? location.trim() : null,
            pageable
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Startup> getStartupsByFounder(User founder) {
        return startupRepository.findByFounder(founder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Startup> findAllStartups() {
        return startupRepository.findAllWithFounder();
    }

    @Override
    public Startup verifyStartup(Long startupId) {
        Startup startup = getStartupById(startupId);
        startup.setVerificationStatus(VerificationStatus.VERIFIED);
        startup.setActive(true);
        return startupRepository.save(startup);
    }

    @Override
    public Startup markAsSpam(Long startupId) {
        Startup startup = getStartupById(startupId);
        startup.setVerificationStatus(VerificationStatus.SPAM);
        startup.setActive(false);
        return startupRepository.save(startup);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByFounder(User founder) {
        return startupRepository.countByFounder(founder);
    }

    private void mapRequest(Startup startup, StartupRequest request) {
        startup.setTitle(request.getTitle());
        startup.setDescription(request.getDescription());
        startup.setIndustry(request.getIndustry());
        startup.setProblemStatement(request.getProblemStatement());
        startup.setSolution(request.getSolution());
        startup.setFundRequired(request.getFundRequired());
        startup.setEquityOffered(request.getEquityOffered());
        startup.setStage(request.getStage());
        startup.setLocation(request.getLocation());
        startup.setBusinessModel(request.getBusinessModel());
        startup.setFinancialDetails(request.getFinancialDetails());
        String logoPath = fileStorageService.store(request.getLogoFile(), "logos");
        if (logoPath != null) {
            startup.setLogo(logoPath);
        }
        String pitchDeckPath = fileStorageService.store(request.getPitchDeckFile(), "pitch-decks");
        if (pitchDeckPath != null) {
            startup.setPitchDeck(pitchDeckPath);
        }
    }
}