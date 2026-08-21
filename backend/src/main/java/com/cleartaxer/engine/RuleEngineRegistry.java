package com.cleartaxer.engine;

import com.cleartaxer.model.AssessmentYear;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class RuleEngineRegistry {

    private final Map<AssessmentYear, TaxRuleEngine> engines;

    public RuleEngineRegistry(List<TaxRuleEngine> ruleEngineList) {
        this.engines = ruleEngineList.stream()
                .collect(Collectors.toMap(TaxRuleEngine::getAssessmentYear, e -> e));
    }

    public TaxRuleEngine getEngine(AssessmentYear assessmentYear) {
        TaxRuleEngine engine = engines.get(assessmentYear);
        if (engine == null) {
            // Fallback to latest engine
            return engines.getOrDefault(AssessmentYear.AY_2024_25, engines.values().iterator().next());
        }
        return engine;
    }

    public Map<AssessmentYear, TaxRuleEngine> getAllEngines() {
        return engines;
    }
}
