package com.home.model;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @NotNull(message = "Идентификатор ордера пустой")
    @Min(value = 0, message = "Идентификатор ордера меньше 0")
    private Long id;
    @NotNull(message = "Сумма ордера пустая")
    @Min(value = 0, message = "Сумма ордера меньше 0")
    private Double amount;
    @NotNull(message = "Валюта суммы ордера пустая")
    private Currency currency;
    @NotBlank(message = "Комментарий по ордеру пустой")
    private String comment;
    private String filename;
    private long line;
    private String result;

    public Order(QueueType queueType, Long id, Double amount, Currency currency, String comment) {
        this.id = id;
        this.amount = amount;
        this.currency = currency;
        this.comment = comment;
        this.line = queueType.getLineNumber();
        this.filename = queueType.getFilename();
    }

    public void validateOrder(Validator validator, List<String> errors) {
        Set<ConstraintViolation<Order>> violations = validator.validate(this);
        if (!violations.isEmpty()) {
            errors.addAll(violations.stream()
                                    .map(ConstraintViolation::getMessage)
                                    .collect(Collectors.toList()));
        }
        this.setResult(errors.isEmpty() ? "OK" : String.join(", ", errors));
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\":" + id +
                ", \"amount\":" + amount +
                ", \"comment\":\"" + comment + '\"' +
                ", \"filename\":\"" + filename + '\"' +
                ", \"line\":" + line +
                ", \"result\":\"" + result + "\"" +
                '}';
    }
}
