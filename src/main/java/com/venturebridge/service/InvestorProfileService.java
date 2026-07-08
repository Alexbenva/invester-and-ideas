package com.venturebridge.service;

import com.venturebridge.dto.InvestorProfileRequest;
import com.venturebridge.entity.InvestorProfile;
import com.venturebridge.entity.User;

public interface InvestorProfileService {

    InvestorProfile upsertProfile(User investor, InvestorProfileRequest request);

    InvestorProfile getProfile(User investor);
}