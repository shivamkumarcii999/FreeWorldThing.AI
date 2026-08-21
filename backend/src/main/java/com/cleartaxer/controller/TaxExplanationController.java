package com.cleartaxer.controller;

import com.cleartaxer.model.TaxExplanationRequest;
import com.cleartaxer.model.TaxExplanationResponse;
import com.cleartaxer.service.TaxExplanationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tax")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class TaxExplanationController {

    private final TaxExplanationService explanationService;

    @PostMapping("/explain")
    public ResponseEntity<TaxExplanationResponse> explainTax(@RequestBody TaxExplanationRequest request) {
        TaxExplanationResponse response = explanationService.explainCalculation(request);
        return ResponseEntity.ok(response);
    }
}
