package com.venturebridge.service;

import com.venturebridge.entity.Role;
import com.venturebridge.entity.User;

import java.util.List;

public interface UserService {

    User getByEmail(String email);

    User getById(Long id);

    User getCurrentUser();

    List<User> findByRole(Role role);

    List<User> findAll();
}