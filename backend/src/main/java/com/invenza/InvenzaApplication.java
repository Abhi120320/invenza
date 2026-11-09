package com.invenza;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Main application entry point for Invenza Backend.
 */
@SpringBootApplication
@EnableAsync
public class InvenzaApplication {

    public static void main(String[] args) {
        SpringApplication.run(InvenzaApplication.class, args);
    }
}

