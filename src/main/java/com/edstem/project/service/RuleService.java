package com.edstem.project.service;

import com.edstem.project.contract.request.RuleRequest;
import com.edstem.project.contract.response.RuleResponse;
import com.edstem.project.controller.RuleController;
import com.edstem.project.exception.CustomException;
import com.edstem.project.model.Action;
import com.edstem.project.model.Condition;
import com.edstem.project.model.Rule;
import com.edstem.project.repository.RuleRepository;
import jakarta.validation.Payload;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RuleService {


    private final RuleRepository ruleRepository;


    public RuleResponse createRule(RuleRequest request) {
        try {
            ModelMapper modelMapper = new ModelMapper();

            Rule rule = modelMapper.map(request.getRule(), Rule.class);

            ruleRepository.save(rule);

            return new RuleResponse("Success", "Rule created successfully");
        } catch (Exception e) {
            return new RuleResponse("Error", "Failed to create the rule: " + e.getMessage());
        }
    }
}
