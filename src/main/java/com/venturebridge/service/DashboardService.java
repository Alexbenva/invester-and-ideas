package com.venturebridge.service;

import com.venturebridge.dto.DashboardStatsDto;
import com.venturebridge.entity.User;

public interface DashboardService {

    DashboardStatsDto buildAdminStats();

    DashboardStatsDto buildFounderStats(User founder);

    DashboardStatsDto buildInvestorStats(User investor);
}