package com.edstem.project.contract.request;

import com.edstem.project.model.Rule;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class Items {
    private String productId;
    private String name;
    private Double price;
    private Double quantity;
}