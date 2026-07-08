package com.venturebridge.service;

import com.venturebridge.dto.ChangePasswordRequest;
import com.venturebridge.dto.ForgotPasswordRequest;
import com.venturebridge.dto.RegisterRequest;
import com.venturebridge.entity.User;

public interface AuthService {

    User register(RegisterRequest request);

    String requestPasswordReset(ForgotPasswordRequest request);

    void resetPassword(String token, String newPassword, String confirmPassword);

    void changePassword(String email, ChangePasswordRequest request);
}