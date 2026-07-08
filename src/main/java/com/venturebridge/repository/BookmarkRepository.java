package com.venturebridge.repository;

import com.venturebridge.entity.Bookmark;
import com.venturebridge.entity.Startup;
import com.venturebridge.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    Optional<Bookmark> findByInvestorAndStartup(User investor, Startup startup);

    List<Bookmark> findByInvestor(User investor);

    @org.springframework.data.jpa.repository.Query("SELECT b FROM Bookmark b JOIN FETCH b.startup WHERE b.investor = :investor")
    List<Bookmark> findByInvestorWithStartup(@org.springframework.data.repository.query.Param("investor") User investor);

    long countByInvestor(User investor);
}