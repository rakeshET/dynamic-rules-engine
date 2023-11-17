package com.edstem.project.contract.response;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryReplenishmentResponse {
    private String productId;
    private Integer stockLevel;
}
