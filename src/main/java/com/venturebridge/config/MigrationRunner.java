package com.venturebridge.config;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class MigrationRunner implements ApplicationListener<ContextRefreshedEvent> {

    private final JdbcTemplate jdbcTemplate;

    public MigrationRunner(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            jdbcTemplate.execute("ALTER TABLE interests MODIFY COLUMN status ENUM('PENDING', 'ACCEPTED', 'REJECTED', 'WITHDRAWN', 'ACTIVE', 'CANCELLED') NOT NULL");
            jdbcTemplate.execute("UPDATE interests SET status = 'PENDING' WHERE status = 'ACTIVE'");
            jdbcTemplate.execute("UPDATE interests SET status = 'WITHDRAWN' WHERE status = 'CANCELLED'");
            jdbcTemplate.execute("ALTER TABLE interests MODIFY COLUMN status ENUM('PENDING', 'ACCEPTED', 'REJECTED', 'WITHDRAWN') NOT NULL");
            System.out.println("VentureBridge Migration Success: Converted ACTIVE/CANCELLED statuses to PENDING/WITHDRAWN.");
        } catch (Exception e) {
            System.err.println("VentureBridge Migration Warning: " + e.getMessage());
        }
    }
}
