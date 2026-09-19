package com.freeworldthing.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Routes AI features to the configured provider. Today: the deterministic mock
 * engine. Tomorrow: an OpenAiGateway / AnthropicGateway implementing AiGateway,
 * selected by the fwt.ai.provider property — with automatic mock fallback when
 * the provider is unreachable or no key is configured.
 */
@Service
public class AiOrchestrator {

    private final AiGateway gateway;

    public AiOrchestrator(MockAiEngine mockEngine,
                          @Value("${fwt.ai.provider:mock}") String provider,
                          @Value("${fwt.ai.mock-fallback:true}") boolean mockFallback) {
        // Provider selection point: extend with real LLM gateways as they're added.
        switch (provider.toLowerCase()) {
            case "openai", "anthropic" -> {
                if (!mockFallback) throw new IllegalStateException(
                        "Provider '" + provider + "' not yet wired. Add an AiGateway implementation for it.");
                this.gateway = mockEngine; // graceful fallback
            }
            default -> this.gateway = mockEngine;
        }
    }

    public AiGateway gateway() {
        return gateway;
    }
}
