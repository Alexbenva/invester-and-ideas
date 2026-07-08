package com.venturebridge.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDto {

    private long totalUsers;
    private long totalStartups;
    private long totalInvestors;
    private long verifiedStartups;
    
    // Founder dashboard specific
    private long founderStartups;
    private long pendingRequests;
    private long acceptedRequests;
    private long rejectedRequests;
    
    // Investor dashboard specific
    private long bookmarkedStartups;
    private long interestedStartups; // total requests
    private long investorAcceptedRequests;
    private long investorPendingRequests;
}