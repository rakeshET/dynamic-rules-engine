package com.edstem.project.service;

import com.edstem.project.contract.request.RuleAction;
import com.edstem.project.contract.request.RuleClauses;
import com.edstem.project.contract.request.RuleCondition;
import com.edstem.project.contract.request.RuleRequest;
import com.edstem.project.contract.response.AllRuleResponse;
import com.edstem.project.contract.response.RuleResponse;
import com.edstem.project.model.Action;
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
    void testUpdateRule() {
        Condition condition = new Condition();
        condition.setClauses(new ArrayList<>());
        condition.setId(1L);
        condition.setType("Type");

        Condition condition2 = new Condition();
        condition2.setClauses(new ArrayList<>());
        condition2.setId(1L);
        condition2.setType("Type");
        Rule rule = mock(Rule.class);
        when(rule.getCondition()).thenReturn(condition2);
        doNothing().when(rule).setActions(Mockito.<List<Action>>any());
        doNothing().when(rule).setCondition(Mockito.<Condition>any());
        doNothing().when(rule).setDescription(Mockito.<String>any());
        doNothing().when(rule).setId(Mockito.<Long>any());
        doNothing().when(rule).setRuleId(Mockito.<String>any());
        rule.setActions(new ArrayList<>());
        rule.setCondition(condition);
        rule.setDescription("The characteristics of someone or something");
        rule.setId(1L);
        rule.setRuleId("42");
        Optional<Rule> ofResult = Optional.of(rule);

        Condition condition3 = new Condition();
        condition3.setClauses(new ArrayList<>());
        condition3.setId(1L);
        condition3.setType("Type");

        Rule rule2 = new Rule();
        rule2.setActions(new ArrayList<>());
        rule2.setCondition(condition3);
        rule2.setDescription("description");
        rule2.setId(1L);
        rule2.setRuleId("42");
        when(ruleRepository.save(Mockito.<Rule>any())).thenReturn(rule2);
        when(ruleRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);

        RuleClauses ruleClauses = new RuleClauses();
        ruleClauses.setField("Error");
        ruleClauses.setOperation("Error");
        ruleClauses.setValue("42");

        ArrayList<RuleClauses> clauses = new ArrayList<>();
        clauses.add(ruleClauses);

        RuleCondition conditionType = new RuleCondition();
        conditionType.setClauses(clauses);
        conditionType.setType("Error");

        RuleAction ruleAction = new RuleAction();
        ruleAction.setActionType("Success");
        ruleAction.setActionValue(10.0d);

        ArrayList<RuleAction> actions = new ArrayList<>();
        actions.add(ruleAction);

        RuleRequest request = new RuleRequest("42", "description", new RuleCondition(),
                actions);
        request.setConditionType(conditionType);
        RuleResponse actualUpdateRuleResult = ruleService.updateRule(1L, request);
        verify(rule).getCondition();
        verify(rule, atLeast(1)).setActions(Mockito.<List<Action>>any());
        verify(rule).setCondition(Mockito.<Condition>any());
        verify(rule, atLeast(1)).setDescription(Mockito.<String>any());
        verify(rule).setId(Mockito.<Long>any());
        verify(rule).setRuleId(Mockito.<String>any());
        verify(ruleRepository).findById(Mockito.<Long>any());
        verify(ruleRepository).save(Mockito.<Rule>any());
        assertEquals("Rule updated successfully", actualUpdateRuleResult.getMessage());
        assertEquals("Success", actualUpdateRuleResult.getStatus());
    }

    @Test
    void testGetAllRules() {
        when(ruleRepository.findAll()).thenReturn(new ArrayList<>());
        List<AllRuleResponse> actualAllRules = ruleService.getAllRules();
        verify(ruleRepository).findAll();
        assertTrue(actualAllRules.isEmpty());
    }
}