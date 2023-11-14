package com.edstem.project.contract.response;

import com.edstem.project.contract.request.RuleAction;
import com.edstem.project.contract.request.RuleCondition;
import com.edstem.project.model.Action;
import com.edstem.project.model.Rule;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AllRuleResponse {
    private Long id;
//    private String message;
    private String ruleId;
    private String description;
    private RuleCondition conditionType;
    private List<RuleAction> actions;
}