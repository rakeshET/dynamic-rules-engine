package com.edstem.project.service;

import com.edstem.project.contract.request.RuleRequest;
import com.edstem.project.contract.response.RuleResponse;
import com.edstem.project.controller.RuleController;
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
    private final ModelMapper modelMapper;

    public RuleResponse createRule(RuleRequest rule) {
        // Validate rule
        if (rule == null) {
            throw new IllegalArgumentException("Rule cannot be null");
        }

        // Validate ruleId
        if (rule.getId() == null || rule.Id().isEmpty()) {
            throw new IllegalArgumentException("Rule ID cannot be null or empty");
        }

        // Validate condition
        if (rule.getCondition() == null) {
            throw new IllegalArgumentException("Condition cannot be null");
        }

        // Validate actions
        if (rule.getActions() == null || rule.getActions().isEmpty()) {
            throw new IllegalArgumentException("Actions cannot be null or empty");
        }

        // Save and return the rule
        return ruleRepository.save(rule);
    }
}