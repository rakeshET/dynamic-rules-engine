package com.edstem.project.contract.request;


import com.edstem.project.model.Action;
import com.edstem.project.model.Condition;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class EvaluationRequest {

    private String ruleId;
    private String description;
    private Condition condition;
    private List<Action> actions;

}
