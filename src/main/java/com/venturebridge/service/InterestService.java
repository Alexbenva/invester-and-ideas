package com.venturebridge.service;

import com.venturebridge.entity.Interest;
import com.venturebridge.entity.Startup;
import com.venturebridge.entity.User;

import java.util.List;

public interface InterestService {

    Interest expressInterest(User investor, Startup startup);

    Interest expressInterest(User investor, Startup startup, String message, java.math.BigDecimal expectedInvestment);

    Interest cancelInterest(User investor, Startup startup);

    Interest acceptInterest(Long interestId, User founder);

    Interest rejectInterest(Long interestId, User founder);

    Interest withdrawInterest(Long interestId, User investor);

    Interest getById(Long id);

    long countInterestedInvestors(Startup startup);

    long countInvestorInterests(User investor);

    List<Interest> findByInvestor(User investor);

    List<Interest> findPendingByFounder(User founder);

    List<Interest> findByFounder(User founder);
}