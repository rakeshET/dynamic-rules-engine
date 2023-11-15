package com.edstem.project.contract.response;

import com.edstem.project.contract.request.RuleAction;
import com.edstem.project.contract.request.RuleCondition;
import lombok.Getter;

import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AllRuleResponse {
    private Long id;
    private String ruleId;
    private String description;
    private RuleCondition condition;
    private List<RuleAction> actions;
}