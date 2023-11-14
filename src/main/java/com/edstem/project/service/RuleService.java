package com.edstem.project.service;

import com.edstem.project.contract.request.RuleAction;
import com.edstem.project.contract.request.RuleClauses;
import com.edstem.project.contract.request.RuleRequest;
import com.edstem.project.contract.response.*;
import com.edstem.project.model.Action;
import com.edstem.project.model.Clause;
import com.edstem.project.model.Condition;
import com.edstem.project.model.Rule;
import com.edstem.project.repository.RuleRepository;
import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;

import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RuleService {


    private final RuleRepository ruleRepository;
    private final ModelMapper modelMapper;


    public RuleResponse createRule(RuleRequest request) {
        try {
            Rule rule = modelMapper.map(request, Rule.class);
            ruleRepository.save(rule);

            return new RuleResponse("Success", "Rule created successfully");
        } catch (Exception e) {
            return new RuleResponse("Error", "Failed to create the rule: " + e.getMessage());
        }
    }

    public List<AllRuleResponse> getAllRules() {
        List<Rule> rules = ruleRepository.findAll();
        return rules.stream()
                .map(rule -> convertToAllRuleResponse(rule))
                .collect(Collectors.toList());
    }

    private AllRuleResponse convertToAllRuleResponse(Rule rule) {
        AllRuleResponse response = modelMapper.map(rule, AllRuleResponse.class);

        List<RuleAction> ruleActions = rule.getActions().stream()
                .map(action -> modelMapper.map(action, RuleAction.class))
                .collect(Collectors.toList());
        response.setActions(ruleActions);

        if (response.getCondition() != null) {
            List<RuleClauses> ruleClauses = rule.getCondition().getClauses().stream()
                    .map(clause -> modelMapper.map(clause, RuleClauses.class))
                    .collect(Collectors.toList());
            response.getCondition().setClauses(ruleClauses);
            response.getCondition().setType(rule.getCondition().getType());
        }

        response.setId(rule.getId()); // Explicitly set the id
        return response;
    }



    public void deleteRule(Long id) {
        ruleRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Rule not found with id: " + id));
            ruleRepository.deleteById(id);

    }




    public Object evaluateRules(Map<String, Object> inputData) {
        List<Rule> allRules = ruleRepository.findAll();
        Object response=new Object();
        List<Rule> matchedRules = new ArrayList<>();

        for (Rule rule : allRules) {
            if (evaluateRule(rule, inputData)) {
                 response=evaluateAction(rule.getActions(),inputData);

                 break;

            }
        }

        return response;
    }



    private boolean evaluateRule(Rule rule, Map<String, Object> inputData) {
        // Implement the logic to evaluate the rule based on the input data

        Condition condition = rule.getCondition();
        if (condition == null) {

            return true;
        }

        return evaluateCondition(condition, inputData);
    }

    private boolean evaluateCondition(Condition condition, Map<String, Object> inputData) {


        String conditionType = condition.getType();
        List<Clause> clauses = condition.getClauses();


        if ("AND".equalsIgnoreCase(conditionType)) {
            for (Clause clause : clauses) {
                if (!evaluateClause(clause, inputData)) {
                    return false;
                }
            }
            return true;
        } else if ("OR".equals(condition.getType())) {
            for (Clause clause : condition.getClauses()) {
                if (clause!=null){
                if (evaluateClause(clause,inputData)) {
                    return true;
                }}
            }
            return false;


        } else if(condition.getType() == null){
            for (Clause clause : condition.getClauses()) {
                if (clause!=null){
                if (evaluateClause(clause,inputData)) {
                    return true;
                }}
            }
            return true;
        }
        return true;
    }

    private boolean evaluateClause(Clause clause, Map<String, Object> inputData) {


        String field = clause.getField();
        String operation = clause.getOperation();
        String value = clause.getValue();

        Object actualValue = getFieldValue(field, inputData);
        if (actualValue == null) {
            return false;
        }

        switch (operation.toUpperCase()) {
            case "EQUALS":
                return value.equals(actualValue);
            case "GREATER_THAN":
                return Double.parseDouble(value) < Double.parseDouble(actualValue.toString()) ;
            case "LESS_THAN":
                return Double.parseDouble(value) > Double.parseDouble(actualValue.toString());
            case "GREATER_THAN_EQUAL":
                return Double.parseDouble(value) <= Double.parseDouble(actualValue.toString());
            // Add more cases for other operations
            default:
                return false;
        }

    }

    private Object getFieldValue(String field, Map<String, Object> inputData) {

        String[] fieldPath = field.split("\\.");
        Object value = inputData;

        for (String path : fieldPath) {
            if (value instanceof Map) {
                value = ((Map<?, ?>) value).get(path);
            } else if (value instanceof List) {
                int index = Integer.parseInt(path);
                value = ((List<?>) value).get(index);
            } else {
                value = null;
                break;
            }
        }

        return value;
    }

    private Object evaluateAction(List<Action> actions, Map<String, Object> inputData) {
        for (Action action : actions) {
            switch (action.getActionType().toUpperCase()) {
                case "DISCOUNT":
                    return evaluateDiscount(action, inputData);
                case "FLAG_FOR_REVIEW":
                    return evaluateFlagForReview(action, inputData);
                case "REPLENISH_STOCK":
                    if(inputData.containsKey("product")){
                        return evaluateReplenishStock(action, inputData);
                    }
                case "RENEW_MEMBERSHIP":
                    if(inputData.containsKey("customer")){
                        return renewMemberShip(action, inputData);
                    }

                default:
                    throw new IllegalArgumentException("Unknown action type: " + action);
            }
        }
        return null;
    }

    private Object evaluateDiscount(Action action, Map<String, Object> inputData) {
        Map<String, Object> order = (Map<String, Object>) inputData.get("order");
        Double orderTotal = (Double)  order.get("total");
        String orderId=(String) inputData.get("orderId");
        double discountAmount=orderTotal - (orderTotal * (action.getActionValue()) / 100);
        return new DiscountResponse(orderId, discountAmount);
    }

    private FraudDetectionResponse evaluateFlagForReview(Action action, Map<String, Object> inputData) {
        String orderId=(String) inputData.get("orderId");
        Map<String, Object> order = (Map<String, Object>) inputData.get("order");
        Double amount=(Double) order.get("amount");
        return new FraudDetectionResponse(orderId, amount, true);
    }

    private Object evaluateReplenishStock(Action action, Map<String, Object> inputData) {
        Map<String, Object> product = (Map<String, Object>) inputData.get("product");
       double stockLevel = (Integer) product.get("stockLevel");
       String productId=(String) inputData.get("productId");
       stockLevel=stockLevel + action.getActionValue();



        return new ProductResponse(productId, 53);
    }

    private MembershipRenewalResponse renewMemberShip(Action action, Map<String, Object> inputData) {
        Map<String, Object> customer = (Map<String, Object>) inputData.get("customer");

        Integer loyaltyPoint =(Integer)customer.get("loyaltyPoints");
        String customerId=(String) customer.get("id");
        return new MembershipRenewalResponse(customerId, loyaltyPoint,"Premium",true);
    }

    private double getDiscount(double amount, String percentage) {
        return amount - (amount * (Double.parseDouble(percentage) / 100));
    }

    private double replenishStock(double stockLevel, String percentage) {
        return stockLevel + Double.parseDouble(percentage);
    }

//
//    private  double getDiscount(double amount,String percentage){
//        return amount-(amount*(Double.parseDouble(percentage)/100));
//
//    }
//    private double replenishStock(double stockLevel, String percentage){
//        return  stockLevel + Double.parseDouble(percentage);
//
//
//    }
//    public FraudDetectionResponse checkOrderForFraud(FraudOrder fraudOrder) {
//        if (fraudOrder.getOrder().getAmount() > 5000) {
//            return   flagOrderForReview(fraudOrder);
//        }
//        return new FraudDetectionResponse(fraudOrder.getOrderId(), fraudOrder.getOrder().getAmount(),false);
//    }
}
//    public Object evaluateRule(Payload payload) {
//        List<Rule> rules = ruleRepository.findAll();
//
//        for (Rule rule : rules) {
//            if (isConditionMet(rule.getCondition(), payload)) {
//                return executeAction(rule.getActions(), payload);
//            }
//        }
//        return new EvaluationResponse();
//    }
//
//    private boolean isConditionMet(Condition condition, Payload payload) {
//        String field;
//        for (Clause clause : condition.getClauses()) {
//            // Get the field, operation, and value from the clause
//            field = clause.getField();
//            String operation = clause.getOperation();
//            Object value = clause.getValue();
//            Object actualValue = getFieldValueFromPayload(payload, field);
//
//            switch (operation) {
//                case "EQUALS":
//                    return actualValue.equals(value);
//                case "GREATER_THAN":
//                    return ((Comparable) actualValue).compareTo(value) > 0;
//                case "LESS_THAN":
//                    return ((Comparable) actualValue).compareTo(value) < 0;
//                default:
//                    throw new IllegalArgumentException("Invalid operation: " + operation);
//            }
//        }
//
//        return false;
//    }
//
//    private Object getFieldValueFromPayload(Payload payload, String field) {
//        try {
//            String[] parts = field.split("\\.");
//
//            Object currentObject = payload;
//
//            for (String part : parts) {
//                Field f = currentObject.getClass().getDeclaredField(part);
//                f.setAccessible(true);
//                currentObject = f.get(currentObject);
//
//                if (currentObject == null) {
//                    return null;
//                }
//            }
//
//            return currentObject;
//        } catch (NoSuchFieldException | IllegalAccessException e) {
//            throw new RuntimeException("Error getting field value from payload", e);
//        }
//    }
//
//    private Object executeAction(List<Action> actions, Payload payload) {
//        List<Object> results = new ArrayList<>();
//
//        for (Action action : actions) {
//            results.add(executeSingleAction(action, payload));
//        }
//
//        return results;
//    }
//
//    private Object executeSingleAction(Action action, Payload payload) {
//        String actionType = action.getActionType();
//
//        switch (actionType) {
//            case "DISCOUNT":
//                return applyDiscount(action, payload);
//            case "FLAG_FOR_REVIEW":
//                return flagForReview(action, payload);
//            default:
//                throw new IllegalArgumentException("Invalid action type: " + actionType);
//        }
//    }
//
//    private Object applyDiscount(Action action, com.edstem.project.contract.request.Payload payload) {
//        double discountPercent = Double.parseDouble((String) action.getActionValue());
//
//        double discountAmount = payload.getOrder().getTotal() * discountPercent / 100;
//
//        payload.getOrder().setTotal(payload.getOrder().getTotal() - discountAmount);
//
//        return payload;
//    }
//
//
//    private Review flagForReview(Action action, Payload payload) {
//        Review review = new Review();
//
//        review.setOrderId(payload.getClass().getModifiers());
//        review.setReason((String) action.getActionValue());
//
//
//        return review;
//    }
//}