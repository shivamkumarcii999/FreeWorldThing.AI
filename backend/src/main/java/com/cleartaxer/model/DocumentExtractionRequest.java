package com.cleartaxer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentExtractionRequest {
    private String documentText;
    private String documentFileName;
    private String presetTemplateKey; // e.g. "FORM_16_SAMPLE_IT", "SALARY_SLIP_SAMPLE"
}
