package com.cleartaxer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HraDetails {
    @Builder.Default
    private double basicSalary = 0.0;
    @Builder.Default
    private double dearnessAllowance = 0.0;
    @Builder.Default
    private double hraReceived = 0.0;
    @Builder.Default
    private double rentPaid = 0.0;
    @Builder.Default
    private MetroType cityType = MetroType.NON_METRO;
}
