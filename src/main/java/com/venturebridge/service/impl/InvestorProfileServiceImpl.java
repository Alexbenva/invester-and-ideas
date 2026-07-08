package com.venturebridge.service.impl;

import com.venturebridge.dto.InvestorProfileRequest;
import com.venturebridge.entity.InvestorProfile;
import com.venturebridge.entity.User;
import com.venturebridge.exception.ResourceNotFoundException;
import com.venturebridge.repository.InvestorProfileRepository;
import com.venturebridge.service.InvestorProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class InvestorProfileServiceImpl implements InvestorProfileService {

    private final InvestorProfileRepository investorProfileRepository;

    @Override
    public InvestorProfile upsertProfile(User investor, InvestorProfileRequest request) {
        InvestorProfile profile = investorProfileRepository.findByUser(investor).orElse(new InvestorProfile());
        profile.setUser(investor);
        profile.setOrganization(request.getOrganization());
        profile.setInvestmentMin(request.getInvestmentMin());
        profile.setInvestmentMax(request.getInvestmentMax());
        profile.setExperience(request.getExperience());
        profile.setBio(request.getBio());
        profile.setIndustriesInterested(request.getIndustriesInterested());
        return investorProfileRepository.save(profile);
    }

    @Override
    @Transactional(readOnly = true)
    public InvestorProfile getProfile(User investor) {
        return investorProfileRepository.findByUser(investor)
                .orElseThrow(() -> new ResourceNotFoundException("Investor profile not found"));
    }
}