package com.edstem.project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RuleController {

    private RuleService ruleService;

    @PostMapping("/rules")
    public ResponseEntity<Rule> createRule(@RequestBody Rule rule) {
        return ResponseEntity.ok(ruleService.createRule(rule));
    }

    @PostMapping("/evaluate")
    public ResponseEntity<?> evaluate(@RequestBody Payload payload) {
        return ResponseEntity.ok(ruleService.evaluate(payload));
    }
}