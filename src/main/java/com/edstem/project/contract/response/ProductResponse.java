package com.edstem.project.contract.response;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {
    private String productId;
    private Double stockLevel;
}
