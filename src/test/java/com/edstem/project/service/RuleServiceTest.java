package com.edstem.project.service;

import com.edstem.project.contract.request.RuleRequest;
import com.edstem.project.contract.response.AllRuleResponse;
import com.edstem.project.contract.response.RuleResponse;
import com.edstem.project.model.Rule;
import com.edstem.project.repository.RuleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
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

    @Test
    public void testGetAllRules() {
        Rule rule1 = new Rule();
        Rule rule2 = new Rule();
        List<Rule> rules = Arrays.asList(rule1, rule2);

        when(ruleRepository.findAll()).thenReturn(rules);

        List<AllRuleResponse> responses = ruleService.getAllRules();

        verify(ruleRepository, times(1)).findAll();
        assertEquals(rules.size(), responses.size());
    }
    @Test
    void testDeleteRule_SuccessfulDeletion() {

        Long ruleId = 1L;
        when(ruleRepository.findById(ruleId)).thenReturn(Optional.of(new Rule()));
        ruleService.deleteRule(ruleId);
        verify(ruleRepository, times(1)).deleteById(ruleId);
    }
    @Test
    void testDeleteRule_RuleNotFound() {

        Long ruleId = 1L;
        when(ruleRepository.findById(ruleId)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                ruleService.deleteRule(ruleId)
        );
        assertEquals("Rule not found with id: " + ruleId, exception.getMessage());
        verify(ruleRepository, never()).deleteById(anyLong());
    }
}