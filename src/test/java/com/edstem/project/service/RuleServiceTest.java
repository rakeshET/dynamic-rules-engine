package com.edstem.project.service;
import com.edstem.project.contract.request.RuleCondition;
import com.edstem.project.contract.request.RuleRequest;
import com.edstem.project.contract.response.AllRuleResponse;
import com.edstem.project.contract.response.RuleResponse;
import com.edstem.project.model.Action;
import com.edstem.project.model.Condition;
import com.edstem.project.model.Rule;
import com.edstem.project.repository.RuleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {RuleService.class})
@ExtendWith(SpringExtension.class)
class RuleServiceTest {
    @MockBean
    private JavaMailSender javaMailSender;

    @MockBean
    private ModelMapper modelMapper;

    @MockBean
    private RuleRepository ruleRepository;

    @Autowired
    private RuleService ruleService;

    @Test
    void testCreateRule() {
        when(modelMapper.map(Mockito.<Object>any(), Mockito.<Class<Rule>>any()))
                .thenThrow(new IllegalArgumentException("Success"));
        RuleResponse actualCreateRuleResult = ruleService.createRule(new RuleRequest());
        verify(modelMapper).map(Mockito.<Object>any(), Mockito.<Class<Rule>>any());
        assertEquals("Error", actualCreateRuleResult.getStatus());
        assertEquals("Failed to create the rule: Success", actualCreateRuleResult.getMessage());
    }
    @Test
    void testGetAllRules() {
        Condition condition = new Condition();
        condition.setClauses(new ArrayList<>());
        condition.setId(1L);
        condition.setType("Type");

        Rule rule = new Rule();
        ArrayList<Action> actions = new ArrayList<>();
        rule.setActions(actions);
        rule.setCondition(condition);
        rule.setDescription("something");
        rule.setId(1L);
        rule.setRuleId("42");

        ArrayList<Rule> ruleList = new ArrayList<>();
        ruleList.add(rule);
        when(ruleRepository.findAll()).thenReturn(ruleList);

        RuleCondition condition2 = new RuleCondition();
        condition2.setClauses(new ArrayList<>());
        condition2.setType("Type");

        AllRuleResponse allRuleResponse = new AllRuleResponse();
        allRuleResponse.setActions(new ArrayList<>());
        allRuleResponse.setCondition(condition2);
        allRuleResponse.setDescription("something");
        allRuleResponse.setId(1L);
        allRuleResponse.setRuleId("42");
        when(modelMapper.map(Mockito.<Object>any(), Mockito.<Class<AllRuleResponse>>any())).thenReturn(allRuleResponse);
        List<AllRuleResponse> actualAllRules = ruleService.getAllRules();
        verify(modelMapper).map(Mockito.<Object>any(), Mockito.<Class<AllRuleResponse>>any());
        verify(ruleRepository).findAll();
        AllRuleResponse getResult = actualAllRules.get(0);
        RuleCondition condition3 = getResult.getCondition();
        assertEquals("Type", condition3.getType());
        assertEquals(1, actualAllRules.size());
        assertEquals(1L, getResult.getId().longValue());
        assertEquals(actions, condition3.getClauses());
        assertEquals(actions, getResult.getActions());
    }
@Test
void testUpdateRule() {
    Optional<Rule> emptyResult = Optional.empty();
    when(ruleRepository.findById(Mockito.<Long>any())).thenReturn(emptyResult);
    RuleResponse actualUpdateRuleResult = ruleService.updateRule(1L, new RuleRequest());
    verify(ruleRepository).findById(Mockito.<Long>any());
    assertEquals("Error", actualUpdateRuleResult.getStatus());
    assertEquals("Rule not found with ID: 1", actualUpdateRuleResult.getMessage());
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
        rule.setDescription("something");
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
    void testEvaluateRules() {
        Condition condition = new Condition();
        condition.setClauses(new ArrayList<>());
        condition.setId(1L);
        condition.setType("Type");

        Rule rule = new Rule();
        rule.setActions(new ArrayList<>());
        rule.setCondition(condition);
        rule.setDescription("something");
        rule.setId(1L);
        rule.setRuleId("42");
        when(ruleRepository.findByRuleId(Mockito.<String>any())).thenReturn(rule);
        Object actualEvaluateRulesResult = ruleService.evaluateRules("42", new HashMap<>());
        verify(ruleRepository).findByRuleId(Mockito.<String>any());
        assertEquals("No Rule Matched", ((Map<String, String>) actualEvaluateRulesResult).get("message"));
        assertEquals(1, ((Map<String, String>) actualEvaluateRulesResult).size());
    }

}
