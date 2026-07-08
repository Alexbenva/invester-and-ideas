package com.venturebridge.service.impl;

import com.venturebridge.entity.Role;
import com.venturebridge.entity.User;
import com.venturebridge.exception.ResourceNotFoundException;
import com.venturebridge.repository.UserRepository;
import com.venturebridge.service.UserService;
import com.venturebridge.util.CurrentUserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CurrentUserUtil currentUserUtil;

    @Override
    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }

    @Override
    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }

    @Override
    public User getCurrentUser() {
        return currentUserUtil.getCurrentUser();
    }

    @Override
    public List<User> findByRole(Role role) {
        return userRepository.findByRole(role);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }
}