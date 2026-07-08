package com.venturebridge.repository;

import com.venturebridge.entity.Startup;
import com.venturebridge.entity.User;
import com.venturebridge.entity.VerificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StartupRepository extends JpaRepository<Startup, Long> {

    @org.springframework.data.jpa.repository.Query(value = "SELECT s FROM Startup s LEFT JOIN FETCH s.founder WHERE s.active = true",
           countQuery = "SELECT count(s) FROM Startup s WHERE s.active = true")
    Page<Startup> findByActiveTrue(Pageable pageable);

    @org.springframework.data.jpa.repository.Query(value = "SELECT s FROM Startup s LEFT JOIN FETCH s.founder WHERE s.active = true AND LOWER(s.title) LIKE LOWER(CONCAT('%', :title, '%'))",
           countQuery = "SELECT count(s) FROM Startup s WHERE s.active = true AND LOWER(s.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    Page<Startup> findByActiveTrueAndTitleContainingIgnoreCase(@org.springframework.data.repository.query.Param("title") String title, Pageable pageable);

    List<Startup> findByFounder(User founder);

    long countByFounder(User founder);

    long countByVerificationStatus(VerificationStatus verificationStatus);

    Optional<Startup> findByIdAndFounder(Long id, User founder);

    @org.springframework.data.jpa.repository.Query("SELECT s FROM Startup s LEFT JOIN FETCH s.founder WHERE s.id = :id")
    Optional<Startup> findByIdWithFounder(@org.springframework.data.repository.query.Param("id") Long id);

    @org.springframework.data.jpa.repository.Query("SELECT s FROM Startup s LEFT JOIN FETCH s.founder")
    List<Startup> findAllWithFounder();

    @org.springframework.data.jpa.repository.Query(
        value = "SELECT s FROM Startup s LEFT JOIN FETCH s.founder WHERE s.active = true " +
           "AND (:searchTerm IS NULL OR :searchTerm = '' OR LOWER(s.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(s.industry) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "AND (:industry IS NULL OR :industry = '' OR LOWER(s.industry) = LOWER(:industry)) " +
           "AND (:stage IS NULL OR s.stage = :stage) " +
           "AND (:minFunding IS NULL OR s.fundRequired >= :minFunding) " +
           "AND (:maxFunding IS NULL OR s.fundRequired <= :maxFunding) " +
           "AND (:location IS NULL OR :location = '' OR LOWER(s.location) LIKE LOWER(CONCAT('%', :location, '%')))",
        countQuery = "SELECT count(s) FROM Startup s WHERE s.active = true " +
           "AND (:searchTerm IS NULL OR :searchTerm = '' OR LOWER(s.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(s.industry) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "AND (:industry IS NULL OR :industry = '' OR LOWER(s.industry) = LOWER(:industry)) " +
           "AND (:stage IS NULL OR s.stage = :stage) " +
           "AND (:minFunding IS NULL OR s.fundRequired >= :minFunding) " +
           "AND (:maxFunding IS NULL OR s.fundRequired <= :maxFunding) " +
           "AND (:location IS NULL OR :location = '' OR LOWER(s.location) LIKE LOWER(CONCAT('%', :location, '%')))"
    )
    Page<Startup> filterStartups(
        @org.springframework.data.repository.query.Param("searchTerm") String searchTerm,
        @org.springframework.data.repository.query.Param("industry") String industry,
        @org.springframework.data.repository.query.Param("stage") com.venturebridge.entity.StartupStage stage,
        @org.springframework.data.repository.query.Param("minFunding") java.math.BigDecimal minFunding,
        @org.springframework.data.repository.query.Param("maxFunding") java.math.BigDecimal maxFunding,
        @org.springframework.data.repository.query.Param("location") String location,
        Pageable pageable
    );
}