package com.edstem.project.contract.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiscountRequest {
    private String orderId;
    private Customer customer;
    private Order order;
}