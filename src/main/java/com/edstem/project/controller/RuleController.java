package com.edstem.project.controller;

import com.edstem.project.contract.request.RuleRequest;
import com.edstem.project.contract.response.RuleResponse;
import com.edstem.project.service.RuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RuleController {

    private RuleService ruleService;

    @PostMapping("/rules")
    public ResponseEntity<RuleResponse> createRule(@RequestBody RuleRequest rule) {
        return ResponseEntity.ok(ruleService.createRule(rule));
    }

}