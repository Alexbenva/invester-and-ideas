package com.venturebridge.service.impl;

import com.venturebridge.dto.DashboardStatsDto;
import com.venturebridge.entity.Role;
import com.venturebridge.entity.User;
import com.venturebridge.entity.VerificationStatus;
import com.venturebridge.entity.InterestStatus;
import com.venturebridge.repository.BookmarkRepository;
import com.venturebridge.repository.InterestRepository;
import com.venturebridge.repository.StartupRepository;
import com.venturebridge.repository.UserRepository;
import com.venturebridge.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final UserRepository userRepository;
    private final StartupRepository startupRepository;
    private final InterestRepository interestRepository;
    private final BookmarkRepository bookmarkRepository;

    @Override
    public DashboardStatsDto buildAdminStats() {
        return new DashboardStatsDto(
                userRepository.count(),
                startupRepository.count(),
                userRepository.findByRole(Role.ROLE_INVESTOR).size(),
                startupRepository.countByVerificationStatus(VerificationStatus.VERIFIED),
                0, 0, 0, 0,
                0, 0, 0, 0
        );
    }

    @Override
    public DashboardStatsDto buildFounderStats(User founder) {
        long founderStartups = startupRepository.countByFounder(founder);
        
        long pendingRequests = startupRepository.findByFounder(founder).stream()
                .mapToLong(startup -> interestRepository.countByStartupAndStatus(startup, InterestStatus.PENDING))
                .sum();
                
        long acceptedRequests = startupRepository.findByFounder(founder).stream()
                .mapToLong(startup -> interestRepository.countByStartupAndStatus(startup, InterestStatus.ACCEPTED))
                .sum();
                
        long rejectedRequests = startupRepository.findByFounder(founder).stream()
                .mapToLong(startup -> interestRepository.countByStartupAndStatus(startup, InterestStatus.REJECTED))
                .sum();

        return new DashboardStatsDto(
                0, 0, 0, 0,
                founderStartups,
                pendingRequests,
                acceptedRequests,
                rejectedRequests,
                0, 0, 0, 0
        );
    }

    @Override
    public DashboardStatsDto buildInvestorStats(User investor) {
        long bookmarked = bookmarkRepository.countByInvestor(investor);
        long interestedStartups = interestRepository.findByInvestor(investor).size();
        long accepted = interestRepository.countByInvestorAndStatus(investor, InterestStatus.ACCEPTED);
        long pending = interestRepository.countByInvestorAndStatus(investor, InterestStatus.PENDING);

        return new DashboardStatsDto(
                0, 0, 0, 0,
                0, 0, 0, 0,
                bookmarked,
                interestedStartups,
                accepted,
                pending
        );
    }
}