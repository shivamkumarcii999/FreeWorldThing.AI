package com.cleartaxer.controller;

import com.cleartaxer.engine.PresumptiveTaxEngine;
import com.cleartaxer.model.*;
import com.cleartaxer.service.TaxCalculatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/tax")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class TaxCalculationController {

    private final TaxCalculatorService taxCalculatorService;
    private final PresumptiveTaxEngine presumptiveTaxEngine;

    @PostMapping("/compare")
    public ResponseEntity<RegimeComparisonResult> compareRegimes(@RequestBody TaxCalculationRequest request) {
        RegimeComparisonResult result = taxCalculatorService.compareRegimes(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/calculate")
    public ResponseEntity<TaxCalculationResult> calculateSingleRegime(
            @RequestBody TaxCalculationRequest request,
            @RequestParam(defaultValue = "NEW") TaxRegime regime) {
        TaxCalculationResult result = taxCalculatorService.calculateSingleRegime(request, regime);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/presumptive")
    public ResponseEntity<PresumptiveTaxResult> calculatePresumptive(@RequestBody PresumptiveTaxRequest request) {
        PresumptiveTaxResult result = presumptiveTaxEngine.calculate(request);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/metadata")
    public ResponseEntity<Map<String, Object>> getMetadata() {
        Map<String, Object> meta = new HashMap<>();
        meta.put("assessmentYears", Arrays.asList(
                Map.of("id", "AY_2024_25", "label", "AY 2024-25", "desc", "FY 2023-24 (Current Filing Year)"),
                Map.of("id", "AY_2025_26", "label", "AY 2025-26", "desc", "FY 2024-25 (Budget 2024 Revised)")
        ));
        meta.put("ageCategories", AgeCategory.values());
        meta.put("metroTypes", MetroType.values());
        meta.put("presumptiveSections", PresumptiveSection.values());
        meta.put("systemVersion", "ClearTaxer-v1.0.0-PROD");
        return ResponseEntity.ok(meta);
    }
}
