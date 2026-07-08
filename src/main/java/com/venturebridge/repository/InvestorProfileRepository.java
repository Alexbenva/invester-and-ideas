package com.venturebridge.repository;

import com.venturebridge.entity.InvestorProfile;
import com.venturebridge.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvestorProfileRepository extends JpaRepository<InvestorProfile, Long> {

    Optional<InvestorProfile> findByUser(User user);
}