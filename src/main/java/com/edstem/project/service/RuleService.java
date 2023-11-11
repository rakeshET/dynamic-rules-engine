package com.edstem.project.service;

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

    public Rule createRule(Rule rule) {
        // Validate rule
        if (rule == null) {
            throw new IllegalArgumentException("Rule cannot be null");
        }

        // Validate ruleId
        if (rule.getRuleId() == null || rule.getRuleId().isEmpty()) {
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
    public Object evaluate(Payload payload) {
        // Fetch all rules from the database
        List<Rule> rules = ruleRepository.findAll();

        // Iterate over each rule
        for (Rule rule : rules) {
            // Check if the rule condition is met
            if (isConditionMet(rule.getCondition(), payload)) {
                // If the condition is met, execute the rule action
                return executeAction(rule.getActions(), payload);
            }
        }
        return new DefaultResponse();
    }
    private boolean isConditionMet(Condition condition, Payload payload) {
        // Extract the field, operation, and value from the condition
        String field = condition.getField();
        String operation = condition.getOperation();
        Object value = condition.getValue();

        // Extract the actual value from the payload using the field
        Object actualValue = getFieldValueFromPayload(payload, field);

        // Compare the actual value with the condition value based on the operation
        switch (operation) {
            case "EQUALS":
                return actualValue.equals(value);
            case "GREATER_THAN":
                return ((Comparable) actualValue).compareTo(value) > 0;
            case "LESS_THAN":
                return ((Comparable) actualValue).compareTo(value) < 0;
            // Add more cases as needed...
            default:
                throw new IllegalArgumentException("Invalid operation: " + operation);
        }
    }
    private Object getFieldValueFromPayload(Payload payload, String field) {
        // Split the field into parts
        String[] parts = field.split("\\.");

        // Start with the payload as the current object
        Object currentObject = payload;

        // Iterate over each part
        for (String part : parts) {
            // Get the value of the part from the current object
            currentObject = getFieldValueFromObject(currentObject, part);

            // If the current object is null, return null
            if (currentObject == null) {
                return null;
            }
        }

        // Return the final value
        return currentObject;
    }

    private Object getFieldValueFromObject(Object object, String field) {
        try {
            // Get the field from the object
            Field f = object.getClass().getDeclaredField(field);

            // Make the field accessible
            f.setAccessible(true);

            // Return the value of the field
            return f.get(object);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            // If there's an exception, return null
            return null;
        }
    }
    private Object executeAction(List<Action> actions, Payload payload) {
        // Create a list to store the results of the actions
        List<Object> results = new ArrayList<>();

        // Iterate over each action
        for (Action action : actions) {
            // Execute the action and add the result to the results list
            results.add(executeSingleAction(action, payload));
        }

        // Return the results
        return results;
    }

    private Object executeSingleAction(Action action, Payload payload) {
        // Get the action type
        String actionType = action.getActionType();

        // Execute the action based on the action type
        switch (actionType) {
            case "DISCOUNT":
                // Implement the logic to apply a discount
                return applyDiscount(action, payload);
            case "FLAG_FOR_REVIEW":
                // Implement the logic to flag for review
                return flagForReview(action, payload);
            // Add more cases as needed...
            default:
                throw new IllegalArgumentException("Invalid action type: " + actionType);
        }
    }
    private Object applyDiscount(Action action, Payload payload) {
        // Extract the discount percent from the action
        double discountPercent = (double) action.getActionValue();

        // Calculate the discount amount
        double discountAmount = payload.getOrder().getTotal() * discountPercent / 100;

        // Apply the discount to the order total
        payload.getOrder().setTotal(payload.getOrder().getTotal() - discountAmount);

        // Return the updated payload
        return payload;
    }

    private Object flagForReview(Action action, Payload payload) {
        // Create a new Review object
        Review review = new Review();

        // Set the order ID and reason in the review
        review.setOrderId(payload.getOrderId());
        review.setReason((String) action.getActionValue());

        // Save the review to the database
        reviewRepository.save(review);

        // Return the review
        return review;
    }


}