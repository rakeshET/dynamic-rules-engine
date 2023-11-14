package com.edstem.project.contract.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscountResponse {
    private String orderId;
    private Double totalAmount;
}
