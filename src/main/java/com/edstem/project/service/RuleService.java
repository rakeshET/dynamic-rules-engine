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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RuleService {


    private final RuleRepository ruleRepository;
    private final ModelMapper modelMapper;
    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;


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
                .map(this::convertToAllRuleResponse)
                .collect(Collectors.toList());
    }

    public RuleResponse updateRule(Long id, RuleRequest request) {
        try {
            Optional<Rule> rule = ruleRepository.findById(id);
            if(rule.isPresent()){
                Rule exsitingRule = rule.get();
            ModelMapper modelMapper = new ModelMapper();
            modelMapper.map(request,exsitingRule);
            ruleRepository.save(exsitingRule);
            return new RuleResponse("Success", "Rule updated successfully");
            } else {
            return new RuleResponse("Error", "Rule not found with ID: " + id);
            }
        } catch (Exception e) {
            return new RuleResponse("Error", "Failed to update the rule: " + e.getMessage());
        }
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

        response.setId(rule.getId());
        return response;
    }


    public void deleteRule(Long id) {
        ruleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rule not found with id: " + id));
        ruleRepository.deleteById(id);

    }

    public Object evaluateRules(String ruleId, Map<String, Object> inputData) {
        Rule rule = ruleRepository.findByRuleId(ruleId);
        return (rule != null && evaluateRule(rule, inputData)) ? evaluateAction(rule.getActions(), inputData) : new HashMap<String, String>(){{put("message", "No Rule Matched");}};
    }

    private boolean evaluateRule(Rule rule, Map<String, Object> inputData) {

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
                if (clause != null) {
                    if (evaluateClause(clause, inputData)) {
                        return true;
                    }
                }
            }
            return false;

        }
        else {
            for (Clause clause : condition.getClauses()) {
                if (clause != null) {
                    if (evaluateClause(clause, inputData)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    private boolean evaluateClause(Clause clause, Map<String, Object> inputData) {


        String field = clause.getField();
        String operation = clause.getOperation();
        String value = clause.getValue();
        Object actualValue = getFieldValue(field, inputData);
        if (actualValue == null) {
            return false;
        }
        return switch (operation.toUpperCase()) {
            case "EQUALS" -> value.equals(actualValue);
            case "GREATER_THAN" -> Double.parseDouble(value) < Double.parseDouble(actualValue.toString());
            case "LESS_THAN" -> Double.parseDouble(value) > Double.parseDouble(actualValue.toString());
            case "GREATER_THAN_EQUAL" -> Double.parseDouble(value) <= Double.parseDouble(actualValue.toString());
            default -> false;
        };

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
            return switch (action.getActionType().toUpperCase()) {
                case "DISCOUNT" -> evaluateDiscount(action, inputData);
                case "FLAG_FOR_REVIEW" -> evaluateFlagForReview(action, inputData);
                case "REPLENISH_STOCK" -> evaluateReplenishStock(action, inputData);
                case "RENEW_MEMBERSHIP" -> renewMemberShip(action, inputData);
                case "SEND_NOTIFICATION" -> sendNotification(action, inputData);
                default -> throw new IllegalArgumentException("Unknown action type: " + action);
            };
        }
        return null;
    }

    private DiscountResponse evaluateDiscount(Action action, Map<String, Object> inputData) {
        Map<String, Object> order = (Map<String, Object>) inputData.get("order");
        String orderId = (String) inputData.get("orderId");
        double orderTotal = (double) order.get("total");
        double discountPercentage = Double.parseDouble(action.getActionValue().get("discountPercent"));
        double discountAmount = orderTotal-orderTotal * discountPercentage / 100.0;
        return new DiscountResponse(orderId, discountAmount);
    }


    private FraudDetectionResponse evaluateFlagForReview(Action action, Map<String, Object> inputData) {
        String orderId = (String) inputData.get("orderId");
        Map<String, Object> order = (Map<String, Object>) inputData.get("order");
        Double amount = (Double) order.get("amount");
        return new FraudDetectionResponse(orderId, amount, true);
    }

    private InventoryReplenishmentResponse evaluateReplenishStock(Action action, Map<String, Object> inputData) {

        Map<String, Object> product = (Map<String, Object>) inputData.get("product");
        int stockLevel = (Integer) product.get("stockLevel");
        String productId = (String) inputData.get("productId");
        int newStockLevel= stockLevel +Integer.parseInt(action.getActionValue().get("quantity"));
        return new InventoryReplenishmentResponse(productId, newStockLevel);

    }

    private MembershipRenewalResponse renewMemberShip(Action action, Map<String, Object> inputData) {
        Map<String, Object> customer = (Map<String, Object>) inputData.get("customer");
        Integer loyaltyPoint = (Integer) customer.get("loyaltyPoints");
        String customerId = (String) customer.get("id");
        String membershipType=action.getActionValue().get("membershipType");
        return new MembershipRenewalResponse(customerId, loyaltyPoint, membershipType, true);

    }

    private Object sendNotification(Action action, Map<String, Object> inputData) {
        Map<String, Object> product = (Map<String, Object>) inputData.get("product");
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        String recipient = action.getActionValue().get("recipient");
        String content = action.getActionValue().get("message").replace("{{product.name}}", (String) product.get("name"));
        String subject = "Low Stock";
        mailMessage.setFrom(sender);
        mailMessage.setTo(recipient);
        mailMessage.setText(content);
        mailMessage.setSubject(subject);
        javaMailSender.send(mailMessage);
        return new HashMap<String, Boolean>() {
            {
                put("success", true);


            }
        };
    }
}