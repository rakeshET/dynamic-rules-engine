package com.edstem.project.controller;

import com.edstem.project.contract.request.RuleRequest;
import com.edstem.project.contract.response.RuleResponse;
import com.edstem.project.service.RuleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public class RuleControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Mock
    private RuleService ruleService;

    @InjectMocks
    private RuleController RuleController;



}