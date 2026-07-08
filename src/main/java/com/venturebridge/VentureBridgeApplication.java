package com.venturebridge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Application entry point for VentureBridge.
 */
@SpringBootApplication
@EnableJpaAuditing
public class VentureBridgeApplication {

    public static void main(String[] args) {
        SpringApplication.run(VentureBridgeApplication.class, args);
    }
}