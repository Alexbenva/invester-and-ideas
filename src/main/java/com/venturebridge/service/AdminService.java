package com.venturebridge.service;

import com.venturebridge.entity.Startup;
import com.venturebridge.entity.User;

import java.util.List;

public interface AdminService {

    List<User> findAllUsers();

    List<User> findFounders();

    List<User> findInvestors();

    Startup verifyStartup(Long startupId);

    Startup markStartupAsSpam(Long startupId);

    void deleteStartup(Long startupId);
}