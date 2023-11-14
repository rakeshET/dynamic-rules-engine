package com.edstem.project.contract.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Review {
    private long id;
    private long orderId;
    private String reason;
}