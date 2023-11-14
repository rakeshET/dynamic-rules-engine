package com.edstem.project.contract.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class RuleCondition {
    private String type;
    private List<RuleClauses> clauses;
}
