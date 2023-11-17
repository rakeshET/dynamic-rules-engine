package com.edstem.project.contract.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FraudDetectionResponse {
    private String orderId;
    private double amount;
    private boolean potentialFraudDetected=false;



}
