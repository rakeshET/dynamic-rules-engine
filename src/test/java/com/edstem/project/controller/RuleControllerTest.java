package com.edstem.project.controller;
import com.edstem.project.contract.request.RuleCondition;
import com.edstem.project.contract.request.RuleRequest;
import com.edstem.project.contract.response.RuleResponse;
import com.edstem.project.service.RuleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {RuleController.class})
@ExtendWith(SpringExtension.class)
public class RuleControllerTest {
    @Autowired
    private RuleController ruleController;

    @MockBean
    private RuleService ruleService;
    @Test
    void testCreateRule() throws Exception {
        when(ruleService.createRule(Mockito.<RuleRequest>any()))
                .thenReturn(new RuleResponse("Status", "sample message"));

        RuleCondition condition = new RuleCondition();
        condition.setClauses(new ArrayList<>());
        condition.setType("Type");

        RuleRequest ruleRequest = new RuleRequest();
        ruleRequest.setActions(new ArrayList<>());
        ruleRequest.setCondition(condition);
        ruleRequest.setDescription("desc");
        ruleRequest.setId(1L);
        ruleRequest.setRuleId("42");
        String content = (new ObjectMapper()).writeValueAsString(ruleRequest);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/v1/rules/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
        MockMvcBuilders.standaloneSetup(ruleController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content()
                        .string("{\"status\":\"Status\",\"message\":\"sample message\"}"));
    }

    @Test
    void testDeleteRule() throws Exception {
        doNothing().when(ruleService).deleteRule(Mockito.<Long>any());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/v1/rules/{id}", 1L);
        MockMvcBuilders.standaloneSetup(ruleController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=ISO-8859-1"))
                .andExpect(MockMvcResultMatchers.content().string("Rule Successfully Deleted"));
    }
    @Test
    void testUpdateRule() throws Exception {
        when(ruleService.updateRule(Mockito.<Long>any(), Mockito.<RuleRequest>any()))
                .thenReturn(new RuleResponse("Status", "sample message"));

        RuleCondition condition = new RuleCondition();
        condition.setClauses(new ArrayList<>());
        condition.setType("Type");

        RuleRequest ruleRequest = new RuleRequest();
        ruleRequest.setActions(new ArrayList<>());
        ruleRequest.setCondition(condition);
        ruleRequest.setDescription("description");
        ruleRequest.setId(1L);
        ruleRequest.setRuleId("42");
        String content = (new ObjectMapper()).writeValueAsString(ruleRequest);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put("/v1/rules/{ruleId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(ruleController).build().perform(requestBuilder);
        actualPerformResult.andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content()
                        .string("{\"status\":\"Status\",\"message\":\"sample message\"}"));
    }

    @Test
    void testGetAllRules() throws Exception {
        when(ruleService.getAllRules()).thenReturn(new ArrayList<>());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/v1/rules/all");
        MockMvcBuilders.standaloneSetup(ruleController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string("[]"));
    }
    @Test
    void testEvaluateRules() throws Exception {
        when(ruleService.evaluateRules(Mockito.<Map<String, Object>>any())).thenReturn("Evaluate Rules");

        HashMap<Object, Object> objectObjectMap = new HashMap<>();
        objectObjectMap.put((Object) "stockLevel", 10.0d);

        HashMap<Object, Object> objectObjectMap2 = new HashMap<>();
        objectObjectMap2.put((Object) "total", 10.0d);
        objectObjectMap2.put((Object) "amount", 10.0d);

        HashMap<Object, Object> objectObjectMap3 = new HashMap<>();
        objectObjectMap3.put((Object) "loyaltyPoints", 1);
        objectObjectMap3.put((Object) "id", "abc");
        objectObjectMap3.put((Object) "loyaltyPoints", "10");

        HashMap<String, Object> stringObjectMap = new HashMap<>();
        stringObjectMap.put((String) "product", objectObjectMap);
        stringObjectMap.put((String) "productId", "abc");
        stringObjectMap.put((String) "order", objectObjectMap2);
        stringObjectMap.put((String) "orderId", "abc");
        stringObjectMap.put((String) "customer", objectObjectMap3);
        stringObjectMap.put((String) "product", "10");
        stringObjectMap.put((String) "customer", "10");
        String content = (new ObjectMapper()).writeValueAsString(stringObjectMap);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/v1/rules/evaluate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
        MockMvcBuilders.standaloneSetup(ruleController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=ISO-8859-1"))
                .andExpect(MockMvcResultMatchers.content().string("Evaluate Rules"));
    }

}
