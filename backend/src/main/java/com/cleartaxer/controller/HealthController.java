package com.cleartaxer.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Lightweight health endpoint used as the Render health check path.
 */
@RestController
@RequestMapping("/api/tax")
public class HealthController {

    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
                "status", "UP",
                "service", "cleartaxer-backend",
                "systemVersion", "ClearTaxer-v1.0.0-PROD"
        );
    }
}
