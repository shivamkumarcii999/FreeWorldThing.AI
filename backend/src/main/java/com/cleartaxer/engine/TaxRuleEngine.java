package com.cleartaxer.engine;

import com.cleartaxer.model.*;

public interface TaxRuleEngine {
    AssessmentYear getAssessmentYear();
    String getVersion();

    TaxCalculationResult calculateOldRegime(TaxCalculationRequest request);
    TaxCalculationResult calculateNewRegime(TaxCalculationRequest request);

    default TaxCalculationResult calculate(TaxCalculationRequest request, TaxRegime regime) {
        return regime == TaxRegime.NEW ? calculateNewRegime(request) : calculateOldRegime(request);
    }
}
