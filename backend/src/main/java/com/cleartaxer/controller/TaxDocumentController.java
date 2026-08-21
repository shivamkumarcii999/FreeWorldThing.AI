package com.cleartaxer.controller;

import com.cleartaxer.model.DocumentExtractionRequest;
import com.cleartaxer.model.ExtractedTaxDocument;
import com.cleartaxer.service.TaxDocumentParserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tax/document")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class TaxDocumentController {

    private final TaxDocumentParserService parserService;

    @PostMapping("/parse")
    public ResponseEntity<ExtractedTaxDocument> parseDocument(@RequestBody DocumentExtractionRequest request) {
        ExtractedTaxDocument document = parserService.parseDocument(request);
        return ResponseEntity.ok(document);
    }

    @GetMapping("/presets")
    public ResponseEntity<List<Map<String, String>>> getPresets() {
        return ResponseEntity.ok(List.of(
                Map.of("key", "FORM_16_SAMPLE_IT", "title", "Form 16: Tech Professional (₹14.5L)", "employer", "Tata Consultancy Services"),
                Map.of("key", "SALARY_SLIP_CONSULTANT", "title", "Salary Slip: Senior Lead (₹24L + NPS)", "employer", "HyperScale Fintech Labs"),
                Map.of("key", "FORM_16_SENIOR", "title", "Form 16: Pensioner (₹5.5L + Interest)", "employer", "SBI Pension Cell")
        ));
    }
}
