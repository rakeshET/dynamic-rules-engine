package com.edstem.project.contract.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MembershipRenewalResponse {
    private String customerId;
    private int loyaltyPoints;
    private String membershipType;
    private boolean renewMembership;
}
