package com.edstem.project.contract.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RuleAction {
    private String actionType;
    private Map<String , String > actionValue = new HashMap<>();

}
