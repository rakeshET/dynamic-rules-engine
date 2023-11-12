package com.edstem.project.contract.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class Customer {
    private String id;
    private String name;
    private String status;

}