package com.venturebridge.service.impl;

import com.venturebridge.entity.Startup;
import com.venturebridge.entity.User;
import com.venturebridge.entity.Role;
import com.venturebridge.repository.StartupRepository;
import com.venturebridge.repository.UserRepository;
import com.venturebridge.service.AdminService;
import com.venturebridge.service.StartupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final StartupService startupService;
    private final StartupRepository startupRepository;

    @Override
    @Transactional(readOnly = true)
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findFounders() {
        return userRepository.findByRole(Role.ROLE_FOUNDER);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findInvestors() {
        return userRepository.findByRole(Role.ROLE_INVESTOR);
    }

    @Override
    public Startup verifyStartup(Long startupId) {
        return startupService.verifyStartup(startupId);
    }

    @Override
    public Startup markStartupAsSpam(Long startupId) {
        return startupService.markAsSpam(startupId);
    }

    @Override
    public void deleteStartup(Long startupId) {
        startupRepository.deleteById(startupId);
    }
}