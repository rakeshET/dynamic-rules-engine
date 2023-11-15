package com.edstem.project.contract.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Items {
    private String productId;
    private String name;
    private Double price;
    private Double quantity;
}