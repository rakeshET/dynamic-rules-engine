package com.edstem.project.controller;
import com.edstem.project.contract.request.RuleRequest;
import com.edstem.project.contract.response.AllRuleResponse;
import com.edstem.project.contract.response.RuleResponse;
import com.edstem.project.model.Rule;
import com.edstem.project.service.RuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rules")
public class RuleController {

    private final RuleService ruleService;


    @PostMapping("/create")
    public ResponseEntity<RuleResponse> createRule(@RequestBody RuleRequest request) {
        RuleResponse response = ruleService.createRule(request);
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String>  deleteRule(@PathVariable Long id) {
         ruleService.deleteRule(id);
         return ResponseEntity.ok("Rule Successfully Deleted");
    }
    @PutMapping("/{id}")
    public ResponseEntity<RuleResponse> updateRule(@PathVariable Long id, @RequestBody RuleRequest request) {
        RuleResponse response = ruleService.updateRule(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<List<AllRuleResponse>> getAllRules() {
        List<AllRuleResponse> response = ruleService.getAllRules();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/evaluate/{ruleId}")
    public ResponseEntity<?> evaluateRules(@PathVariable String ruleId,@RequestBody Map<String, Object> inputData) {
        Object result = ruleService.evaluateRules(ruleId,inputData);
        return ResponseEntity.ok(result);
    }
}
