package com.edstem.project.service;

import com.edstem.project.contract.request.Payload;
import com.edstem.project.contract.request.RuleRequest;
import com.edstem.project.contract.response.AllRuleResponse;
import com.edstem.project.contract.response.EvaluationResponse;
import com.edstem.project.contract.response.Review;
import com.edstem.project.contract.response.RuleResponse;
import com.edstem.project.model.Action;
import com.edstem.project.model.Clause;
import com.edstem.project.model.Condition;
import com.edstem.project.model.Rule;
import com.edstem.project.repository.RuleRepository;
import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RuleService {


    private final RuleRepository ruleRepository;
    private final ModelMapper modelMapper;


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

    public void deleteRule(Long id) {
        Rule rule =
                ruleRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                "Rule not found with id: " + id));
            ruleRepository.deleteById(id);

    }


    public List<AllRuleResponse> getAllRules() {
        List<Rule> rules = ruleRepository.findAll();
        return rules.stream()
                .map(rule -> modelMapper.map(rule, AllRuleResponse.class))
                .collect(Collectors.toList());
    }

    public Object evaluateRule(Payload payload) {
        List<Rule> rules = ruleRepository.findAll();

        for (Rule rule : rules) {
            if (isConditionMet(rule.getCondition(), payload)) {
                return executeAction(rule.getActions(), payload);
            }
        }
        return new EvaluationResponse();
    }
    private boolean isConditionMet(Condition condition, Payload payload) {
        String field;
        for (Clause clause : condition.getClauses()) {
            // Get the field, operation, and value from the clause
            field = clause.getField();
            String operation = clause.getOperation();
            Object value = clause.getValue();
            Object actualValue = getFieldValueFromPayload(payload, field);

            switch (operation) {
                case "EQUALS":
                    return actualValue.equals(value);
                case "GREATER_THAN":
                    return ((Comparable) actualValue).compareTo(value) > 0;
                case "LESS_THAN":
                    return ((Comparable) actualValue).compareTo(value) < 0;
                default:
                    throw new IllegalArgumentException("Invalid operation: " + operation);
            }
        }

        return false;
    }

    private Object getFieldValueFromPayload(Payload payload, String field) {
        try {
            String[] parts = field.split("\\.");

            Object currentObject = payload;

            for (String part : parts) {
                Field f = currentObject.getClass().getDeclaredField(part);
                f.setAccessible(true);
                currentObject = f.get(currentObject);

                if (currentObject == null) {
                    return null;
                }
            }

            return currentObject;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Error getting field value from payload", e);
        }
    }

    private Object executeAction(List<Action> actions, Payload payload) {
        List<Object> results = new ArrayList<>();

        for (Action action : actions) {
            results.add(executeSingleAction(action, payload));
        }

        return results;
    }

    private Object executeSingleAction(Action action, Payload payload) {
        String actionType = action.getActionType();

        switch (actionType) {
            case "DISCOUNT":
                return applyDiscount(action, payload);
            case "FLAG_FOR_REVIEW":
                return flagForReview(action, payload);
            default:
                throw new IllegalArgumentException("Invalid action type: " + actionType);
        }
    }

    private Object applyDiscount(Action action, com.edstem.project.contract.request.Payload payload) {
        double discountPercent = Double.parseDouble((String) action.getActionValue());

        double discountAmount = payload.getOrder().getTotal() * discountPercent / 100;

        payload.getOrder().setTotal(payload.getOrder().getTotal() - discountAmount);

        return payload;
    }


    private Review flagForReview(Action action, Payload payload) {
        Review review = new Review();

        review.setOrderId(payload.getClass().getModifiers());
        review.setReason((String) action.getActionValue());


        return review;
    }


}