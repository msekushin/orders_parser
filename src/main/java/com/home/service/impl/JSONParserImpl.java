package com.home.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.home.model.Currency;
import com.home.model.Order;
import com.home.model.QueueType;
import com.home.service.FileParser;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Validator;
import lombok.extern.slf4j.Slf4j;

import static org.springframework.util.ObjectUtils.isEmpty;

@Slf4j
public class JSONParserImpl implements FileParser {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void convertFromQueue(QueueType queueType, Validator validator) {
        String queueData = queueType.getData();
        if (!isEmpty(queueData)) {
            try {
                JsonNode jsonNode = objectMapper.readTree(queueData);
                if (!jsonNode.isEmpty()) {
                    List<String> errors = new ArrayList<>();
                    Long id = extractId(jsonNode, errors);
                    Double amount = extractAmount(jsonNode, errors);
                    Currency currency = extractCurrency(jsonNode, errors);
                    String comment = extractComment(jsonNode, errors);
                    Order order = new Order(queueType, id, amount, currency, comment);
                    order.validateOrder(validator, errors);
                    System.out.println(order.toString());
                }
            } catch (Exception e) {
                log.error("Not valid line {} in file {} \n{}", queueType.getLineNumber(), queueType.getFilename(), e.getMessage(), e);
            }
        } else {
            log.warn("Empty line {} in file {}", queueType.getLineNumber(), queueType.getFilename());
        }
    }

    private Long extractId(JsonNode jsonNode, List<String> errors) {
        Long id = null;
        try {
            JsonNode orderId = jsonNode.get("orderId");
            if (!isEmpty(orderId)) {
                if (orderId.canConvertToLong()) {
                    id = orderId.asLong(-1);
                } else {
                    errors.add("Неверный формат идентификатора ордера");
                }
            }
        } catch (Exception ex) {
            errors.add("Неверный формат идентификатора ордера");
        }
        return id;
    }

    private Double extractAmount(JsonNode jsonNode, List<String> errors) {
        Double amount = null;
        try {
            JsonNode amountNode = jsonNode.get("amount");
            if (amountNode != null) {
                if (amountNode.canConvertToLong()) {
                    amount = amountNode.asDouble(-1);
                } else {
                    errors.add("Неверный формат суммы ордера");
                }
            }
        } catch (Exception ex) {
            errors.add("Неверный формат суммы ордера");
        }
        return amount;
    }

    private Currency extractCurrency(JsonNode jsonNode, List<String> errors) {
        Currency currency = null;
        try {
            JsonNode currencyNode = jsonNode.get("currency");
            if (currencyNode != null) {
                currency = Currency.valueOfByType(currencyNode.asText());
            }
        } catch (Exception ex) {
            errors.add("Не удалось определить валюту суммы ордера");
        }
        return currency;
    }

    private String extractComment(JsonNode jsonNode, List<String> errors) {
        String comment = null;
        try {
            JsonNode commentNode = jsonNode.get("comment");
            if (commentNode != null) {
                if (commentNode.isTextual()) {
                    comment = commentNode.asText();
                } else {
                    errors.add("Не удалось определить комментарий по ордеру");
                }
            }
        } catch (Exception ex) {
            errors.add("Не удалось определить комментарий по ордеру");
        }
        return comment;
    }
}
