package com.venturebridge.repository;

import com.venturebridge.entity.Interest;
import com.venturebridge.entity.InterestStatus;
import com.venturebridge.entity.Startup;
import com.venturebridge.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InterestRepository extends JpaRepository<Interest, Long> {

    long countByStartupAndStatus(Startup startup, InterestStatus status);

    long countByInvestorAndStatus(User investor, InterestStatus status);

    long countByStatus(InterestStatus status);

    List<Interest> findByInvestor(User investor);

    Optional<Interest> findByStartupAndInvestor(Startup startup, User investor);

    List<Interest> findByStartupFounderAndStatus(User founder, InterestStatus status);

    List<Interest> findByStartupFounder(User founder);

    @org.springframework.data.jpa.repository.Query("SELECT i FROM Interest i JOIN FETCH i.startup s JOIN FETCH s.founder WHERE i.investor = :investor")
    List<Interest> findByInvestorWithStartupAndFounder(@org.springframework.data.repository.query.Param("investor") User investor);

    @org.springframework.data.jpa.repository.Query("SELECT i FROM Interest i JOIN FETCH i.investor inv LEFT JOIN FETCH inv.investorProfile WHERE i.startup.founder = :founder AND i.status = :status")
    List<Interest> findByStartupFounderAndStatusWithInvestor(@org.springframework.data.repository.query.Param("founder") User founder, @org.springframework.data.repository.query.Param("status") InterestStatus status);

    @org.springframework.data.jpa.repository.Query("SELECT i FROM Interest i JOIN FETCH i.investor inv LEFT JOIN FETCH inv.investorProfile WHERE i.startup.founder = :founder")
    List<Interest> findByStartupFounderWithInvestor(@org.springframework.data.repository.query.Param("founder") User founder);
}