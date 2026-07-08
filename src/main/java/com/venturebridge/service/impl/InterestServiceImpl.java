package com.venturebridge.service.impl;

import com.venturebridge.entity.Interest;
import com.venturebridge.entity.InterestStatus;
import com.venturebridge.entity.Startup;
import com.venturebridge.entity.User;
import com.venturebridge.exception.ResourceNotFoundException;
import com.venturebridge.repository.InterestRepository;
import com.venturebridge.service.InterestService;
import com.venturebridge.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InterestServiceImpl implements InterestService {

    private final InterestRepository interestRepository;
    private final NotificationService notificationService;

    @Override
    public Interest expressInterest(User investor, Startup startup) {
        return expressInterest(investor, startup, null, null);
    }

    @Override
    public Interest expressInterest(User investor, Startup startup, String message, BigDecimal expectedInvestment) {
        Interest interest = interestRepository.findByStartupAndInvestor(startup, investor).orElse(new Interest());
        interest.setStartup(startup);
        interest.setInvestor(investor);
        interest.setStatus(InterestStatus.PENDING);
        interest.setMessage(message);
        interest.setExpectedInvestment(expectedInvestment);
        Interest saved = interestRepository.save(interest);

        notificationService.create(startup.getFounder(), 
                "New Investor Request: " + investor.getName() + " has expressed interest in " + startup.getTitle());
        return saved;
    }

    @Override
    public Interest cancelInterest(User investor, Startup startup) {
        Interest interest = interestRepository.findByStartupAndInvestor(startup, investor)
                .orElseThrow(() -> new ResourceNotFoundException("Interest request not found"));
        interest.setStatus(InterestStatus.WITHDRAWN);
        Interest saved = interestRepository.save(interest);

        notificationService.create(startup.getFounder(), 
                "Investor Withdrawn: " + investor.getName() + " withdrew interest in " + startup.getTitle());
        return saved;
    }

    @Override
    public Interest acceptInterest(Long interestId, User founder) {
        Interest interest = interestRepository.findById(interestId)
                .orElseThrow(() -> new ResourceNotFoundException("Interest request not found: " + interestId));
        if (!interest.getStartup().getFounder().getId().equals(founder.getId())) {
            throw new IllegalArgumentException("Unauthorized to accept this request");
        }
        interest.setStatus(InterestStatus.ACCEPTED);
        Interest saved = interestRepository.save(interest);

        notificationService.create(interest.getInvestor(), 
                "Founder Accepted Request: " + founder.getName() + " accepted your interest in " + interest.getStartup().getTitle());
        return saved;
    }

    @Override
    public Interest rejectInterest(Long interestId, User founder) {
        Interest interest = interestRepository.findById(interestId)
                .orElseThrow(() -> new ResourceNotFoundException("Interest request not found: " + interestId));
        if (!interest.getStartup().getFounder().getId().equals(founder.getId())) {
            throw new IllegalArgumentException("Unauthorized to reject this request");
        }
        interest.setStatus(InterestStatus.REJECTED);
        Interest saved = interestRepository.save(interest);

        notificationService.create(interest.getInvestor(), 
                "Founder Rejected Request: " + founder.getName() + " rejected your interest in " + interest.getStartup().getTitle());
        return saved;
    }

    @Override
    public Interest withdrawInterest(Long interestId, User investor) {
        Interest interest = interestRepository.findById(interestId)
                .orElseThrow(() -> new ResourceNotFoundException("Interest request not found: " + interestId));
        if (!interest.getInvestor().getId().equals(investor.getId())) {
            throw new IllegalArgumentException("Unauthorized to withdraw this request");
        }
        interest.setStatus(InterestStatus.WITHDRAWN);
        Interest saved = interestRepository.save(interest);

        notificationService.create(interest.getStartup().getFounder(), 
                "Investor Withdrawn: " + investor.getName() + " withdrew interest in " + interest.getStartup().getTitle());
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public Interest getById(Long id) {
        return interestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Interest not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public long countInterestedInvestors(Startup startup) {
        return interestRepository.countByStartupAndStatus(startup, InterestStatus.PENDING) +
               interestRepository.countByStartupAndStatus(startup, InterestStatus.ACCEPTED);
    }

    @Override
    @Transactional(readOnly = true)
    public long countInvestorInterests(User investor) {
        return interestRepository.findByInvestor(investor).size();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Interest> findByInvestor(User investor) {
        return interestRepository.findByInvestorWithStartupAndFounder(investor);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Interest> findPendingByFounder(User founder) {
        return interestRepository.findByStartupFounderAndStatusWithInvestor(founder, InterestStatus.PENDING);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Interest> findByFounder(User founder) {
        return interestRepository.findByStartupFounderWithInvestor(founder);
    }
}