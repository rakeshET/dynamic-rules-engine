package com.edstem.project.controller;

import com.edstem.project.contract.request.RuleCondition;
import com.edstem.project.contract.request.RuleRequest;
import com.edstem.project.contract.response.RuleResponse;
import com.edstem.project.service.RuleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public class RuleControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private RuleController ruleController;
    @MockBean
    private RuleService ruleService;

    @Autowired ObjectMapper mapper = new ObjectMapper();

    @Test
    void testCreateRule() throws Exception {
        when(ruleService.createRule(Mockito.<RuleRequest>any()))
                .thenReturn(new RuleResponse("Status", "Not all who wander are lost"));

        RuleCondition conditionType = new RuleCondition();
        conditionType.setClauses(new ArrayList<>());
        conditionType.setType("Type");

        RuleRequest ruleRequest = new RuleRequest();
        ruleRequest.setActions(new ArrayList<>());
        ruleRequest.setConditionType(conditionType);
        ruleRequest.setDescription("something");
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
                        .string("{\"status\":\"Status\",\"message\":\"something\"}"));
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
}