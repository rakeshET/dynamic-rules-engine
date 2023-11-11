package com.edstem.project.service;

import com.edstem.project.contract.request.RuleRequest;
import com.edstem.project.contract.response.RuleResponse;
import com.edstem.project.model.Rule;
import com.edstem.project.repository.RuleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RuleServiceTest {

    private RuleRepository ruleRepository;
    private ModelMapper modelMapper;
    private RuleService ruleService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        ruleRepository = Mockito.mock(RuleRepository.class);
        modelMapper = new ModelMapper();
        ruleService =
                new RuleService(ruleRepository, modelMapper);
    }

    @Test
    public void testCreateRule() {
        RuleRequest request = new RuleRequest();
        Rule rule = new Rule();
        request.setRule(rule);

        when(ruleRepository.save(any(Rule.class))).thenReturn(rule);

        RuleResponse response = ruleService.createRule(request);

        verify(ruleRepository, times(1)).save(any(Rule.class));
        assertEquals("Success", response.getStatus());
        assertEquals("Rule created successfully", response.getMessage());
    }

    @Test
    public void testCreateRuleException() {
        RuleRequest request = new RuleRequest();
        Rule rule = new Rule();
        request.setRule(rule);

        when(ruleRepository.save(any(Rule.class))).thenThrow(new RuntimeException("Database error"));

        RuleResponse response = ruleService.createRule(request);

        verify(ruleRepository, times(1)).save(any(Rule.class));
        assertEquals("Error", response.getStatus());
        assertEquals("Failed to create the rule: Database error", response.getMessage());
    }
}