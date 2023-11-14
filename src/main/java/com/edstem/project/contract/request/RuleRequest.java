package com.edstem.project.contract.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RuleRequest {
    private  long id;
    private String ruleId;
    private String description;
    private RuleCondition condition;
    private List<RuleAction> actions;

}
