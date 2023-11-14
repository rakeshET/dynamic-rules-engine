package com.edstem.project.service;

import com.edstem.project.contract.request.RuleAction;
import com.edstem.project.contract.request.RuleCondition;
import com.edstem.project.contract.request.RuleRequest;
import com.edstem.project.contract.response.AllRuleResponse;
import com.edstem.project.contract.response.RuleResponse;
import com.edstem.project.model.Condition;
import com.edstem.project.model.Rule;
import com.edstem.project.repository.RuleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
    void testCreateRule() {
        Condition condition = new Condition();
        condition.setClauses(new ArrayList<>());
        condition.setId(1L);
        condition.setType("Type");

        Rule rule = new Rule();
        rule.setActions(new ArrayList<>());
        rule.setCondition(condition);
        rule.setDescription("The characteristics of someone or something");
        rule.setId(1L);
        rule.setRuleId("42");
        when(ruleRepository.save(Mockito.<Rule>any())).thenReturn(rule);

        RuleAction ruleAction = new RuleAction();
        ruleAction.setActionType("Error");
        ruleAction.setActionValue(10.0d);

        ArrayList<RuleAction> actions = new ArrayList<>();
        actions.add(ruleAction);
        RuleCondition conditionType = new RuleCondition();
        conditionType.setClauses(new ArrayList<>());
        conditionType.setType("Error");

        RuleRequest request = new RuleRequest();
        request.setConditionType(conditionType);
        request.setActions(actions);
        RuleResponse actualCreateRuleResult = ruleService.createRule(request);
        verify(ruleRepository).save(Mockito.<Rule>any());
        assertEquals("Rule created successfully", actualCreateRuleResult.getMessage());
        assertEquals("Success", actualCreateRuleResult.getStatus());
    }
    @Test
    void testDeleteRule() {
        Condition condition = new Condition();
        condition.setClauses(new ArrayList<>());
        condition.setId(1L);
        condition.setType("Type");
        Rule rule = new Rule();
        rule.setActions(new ArrayList<>());
        rule.setCondition(condition);
        rule.setDescription("The characteristics of someone or something");
        rule.setId(1L);
        rule.setRuleId("42");
        Optional<Rule> ofResult = Optional.of(rule);
        doNothing().when(ruleRepository).deleteById(Mockito.<Long>any());
        when(ruleRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);
        ruleService.deleteRule(1L);
        verify(ruleRepository).deleteById(Mockito.<Long>any());
        verify(ruleRepository).findById(Mockito.<Long>any());
    }
    @Test
    void testGetAllRules() {
        when(ruleRepository.findAll()).thenReturn(new ArrayList<>());
        List<AllRuleResponse> actualAllRules = ruleService.getAllRules();
        verify(ruleRepository).findAll();
        assertTrue(actualAllRules.isEmpty());
    }
}