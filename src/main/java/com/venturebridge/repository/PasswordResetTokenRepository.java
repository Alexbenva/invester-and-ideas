package com.venturebridge.repository;

import com.venturebridge.entity.PasswordResetToken;
import com.venturebridge.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findFirstByTokenAndUsedFalse(String token);

    void deleteByUser(User user);
}