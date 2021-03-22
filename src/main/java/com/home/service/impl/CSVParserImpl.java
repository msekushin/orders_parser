package com.home.service.impl;

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
public class CSVParserImpl implements FileParser {

    @Override
    public void convertFromQueue(QueueType queueType, Validator validator) {
        String queueData = queueType.getData();
        String[] data = queueData != null ? queueData.split(",") : null;
        if (data != null) {
            try {
                List<String> errors = new ArrayList<>();
                Long id = extractId(data, errors);
                Double amount = extractAmount(data, errors);
                Currency currency = extractCurrency(data, errors);
                String comment = data.length > 3 ? data[3] : null;
                Order order = new Order(queueType, id, amount, currency, comment);
                order.validateOrder(validator, errors);
                System.out.println(order.toString());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        } else {
            log.warn("Empty line {} in file {}", queueType.getLineNumber(), queueType.getFilename());
        }
    }

    private Long extractId(String[] data, List<String> errors) {
        Long id = null;
        try {
            id = data.length > 0 && !isEmpty(data[0]) ? Long.parseLong(data[0]) : null;
        } catch (Exception ex) {
            errors.add("Неверный формат идентификатора ордера");
        }
        return id;
    }

    private Double extractAmount(String[] data, List<String> errors) {
        Double amount = null;
        try {
            amount = data.length > 1 && !isEmpty(data[1]) ? Double.parseDouble(data[1]) : null;
        } catch (Exception ex) {
            errors.add("Неверный формат суммы ордера");
        }
        return amount;
    }

    private Currency extractCurrency(String[] data, List<String> errors) {
        Currency currency = null;
        try {
            currency = data.length > 2 ? Currency.valueOfByType(data[2]) : null;
        } catch (Exception ex) {
            errors.add("Не удалось определить валюту суммы ордера");
        }
        return currency;
    }
}
