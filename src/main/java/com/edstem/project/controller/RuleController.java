package com.edstem.project.controller;

import com.edstem.project.contract.request.EvaluationRequest;
import com.edstem.project.contract.request.Payload;
import com.edstem.project.contract.request.RuleRequest;
import com.edstem.project.contract.response.AllRuleResponse;
import com.edstem.project.contract.response.EvaluationResponse;
import com.edstem.project.contract.response.RuleResponse;
import com.edstem.project.model.Rule;
import com.edstem.project.repository.RuleRepository;
import com.edstem.project.service.RuleService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/rules")
public class RuleController {

    private final RuleService ruleService;
    private final RuleRepository ruleRepository;

    private final ModelMapper modelMapper;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("admin/create")
    public ResponseEntity<RuleResponse> createRule(@RequestBody RuleRequest request) {
        RuleResponse response = ruleService.createRule(request);
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String>  deleteRule(@PathVariable Long id) {
         ruleService.deleteRule(id);
         return ResponseEntity.ok("Rule Successfully Deleted");
    }
    @PutMapping("/{ruleId}")
    public ResponseEntity<RuleResponse> updateRule(@PathVariable Long ruleId, @RequestBody RuleRequest request) {
        RuleResponse response = ruleService.updateRule(ruleId, request);
        HttpStatus status = response.getStatus().equals("Success") ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(response, status);
    }

    @GetMapping("/all")
    public ResponseEntity<List<AllRuleResponse>> getAllRules() {
        List<AllRuleResponse> response = ruleService.getAllRules();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/evaluate")
    public ResponseEntity<?> evaluateRules(@RequestBody Map<String, Object> inputData) {
        Object result = ruleService.evaluateRules(inputData);
        return ResponseEntity.ok(result);
    }
}