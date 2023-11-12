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

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/rules")
public class RuleController {

    private final RuleService ruleService;
    private final RuleRepository ruleRepository;

    private final ModelMapper modelMapper;

    @PostMapping("/create")
    public ResponseEntity<RuleResponse> createRule(@RequestBody RuleRequest request) {
        RuleResponse response = ruleService.createRule(request);
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/delete/{id}")
    public void deleteRule(@PathVariable Long id) {
         ruleService.deleteRule(id);

    }


    @GetMapping("/all")
    public ResponseEntity<List<AllRuleResponse>> getAllRules() {
        List<AllRuleResponse> response = ruleService.getAllRules();
        return ResponseEntity.ok(response);
    }
    @PostMapping("/check")
    public ResponseEntity<EvaluationResponse> evaluateRule(@RequestBody Payload payload) {
        try {
            boolean isRuleValid = (boolean) ruleService.evaluateRule(payload);
            if (isRuleValid) {
                EvaluationResponse Response = new EvaluationResponse("Rule is valid");
                return new ResponseEntity<>(Response, HttpStatus.OK);
            } else {
                EvaluationResponse Response = new EvaluationResponse("Rule is not valid");
                return new ResponseEntity<>(Response, HttpStatus.BAD_REQUEST);
            }
        } catch (IllegalArgumentException e) {
            EvaluationResponse ruleResponse = new EvaluationResponse(e.getMessage());
            return new ResponseEntity<>(ruleResponse, HttpStatus.BAD_REQUEST);
        }
    }
}