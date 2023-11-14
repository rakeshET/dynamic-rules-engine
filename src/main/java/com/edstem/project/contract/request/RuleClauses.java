package com.edstem.project.contract.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RuleClauses {
    private String field;
    private String operation;
    private String  value;
}
