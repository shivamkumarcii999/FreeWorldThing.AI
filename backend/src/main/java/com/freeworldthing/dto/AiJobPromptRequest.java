package com.freeworldthing.dto;

public class AiJobPromptRequest {
    private String prompt;
    private Long clientId;
    private String preferredCurrency;

    public AiJobPromptRequest() {}

    public String getPrompt() { return prompt; }
    public void setPrompt(String prompt) { this.prompt = prompt; }
    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }
    public String getPreferredCurrency() { return preferredCurrency; }
    public void setPreferredCurrency(String preferredCurrency) { this.preferredCurrency = preferredCurrency; }
}
