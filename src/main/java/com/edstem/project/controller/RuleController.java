package com.edstem.project.controller;

import com.edstem.project.contract.request.RuleRequest;
import com.edstem.project.contract.response.RuleResponse;
import com.edstem.project.service.RuleService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/rules")
public class RuleController {

    private final RuleService ruleService;

    private final ModelMapper modelMapper;

    @PostMapping("/create")
    public ResponseEntity<RuleResponse> createRule(@RequestBody RuleRequest request) {
        RuleResponse response = ruleService.createRule(request);
        return ResponseEntity.ok(response);
    }


}