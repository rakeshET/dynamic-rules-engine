package com.edstem.project.contract.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class RuleAction {
    private String actionType;
    private double actionValue;
}
