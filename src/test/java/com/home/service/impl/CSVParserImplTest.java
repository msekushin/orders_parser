package com.home.service.impl;

import com.home.model.FileExt;
import com.home.model.QueueType;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import javax.validation.Validation;
import javax.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

@TestInstance(Lifecycle.PER_CLASS)
class CSVParserImplTest {

    private ByteArrayOutputStream out;
    private CSVParserImpl csvParser;
    private Validator validator;

    @BeforeAll
    public void initialize() {
        csvParser = new CSVParserImpl();
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @BeforeEach
    public void initBeforeEach() {
        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
    }

    @Test
    void convertFromQueueSuccess() {
        QueueType queueType = new QueueType(FileExt.CSV, "1,100,USD,оплата заказа", 1, "src/test/resources/files/orders1.csv");
        csvParser.convertFromQueue(queueType, validator);
        String sout = out.toString();
        assertThat(sout, containsString("\"result\":\"OK\""));
    }

    @Test
    void convertFromQueueEmptyId() {
        QueueType queueType = new QueueType(FileExt.CSV, ",100,USD,оплата заказа", 2, "src/test/resources/files/orders1.csv");
        csvParser.convertFromQueue(queueType, validator);
        String sout = out.toString();
        assertThat(sout, containsString("Идентификатор ордера пустой"));
    }

    @Test
    void convertFromQueueErrorId() {
        QueueType queueType = new QueueType(FileExt.CSV, "\"asd\",100,USD,оплата заказа", 2, "src/test/resources/files/orders1.csv");
        csvParser.convertFromQueue(queueType, validator);
        String sout = out.toString();
        assertThat(sout, containsString("Неверный формат идентификатора ордера"));
    }

    @Test
    void convertFromQueueNegativeId() {
        QueueType queueType = new QueueType(FileExt.CSV, "-1,100,USD,оплата заказа", 2, "src/test/resources/files/orders1.csv");
        csvParser.convertFromQueue(queueType, validator);
        String sout = out.toString();
        assertThat(sout, containsString("Идентификатор ордера меньше 0"));
    }

    @Test
    void convertFromQueueEmptyAmount() {
        QueueType queueType = new QueueType(FileExt.CSV, "2,,EUR,оплата заказа", 2, "src/test/resources/files/orders1.csv");
        csvParser.convertFromQueue(queueType, validator);
        String sout = out.toString();
        assertThat(sout, containsString("Сумма ордера пустая"));
    }

    @Test
    void convertFromQueueErrorAmount() {
        QueueType queueType = new QueueType(FileExt.CSV, "2,\"asdasd\",EUR,оплата заказа", 2, "src/test/resources/files/orders1.csv");
        csvParser.convertFromQueue(queueType, validator);
        String sout = out.toString();
        assertThat(sout, containsString("Неверный формат суммы ордера"));
    }

    @Test
    void convertFromQueueNegativeAmount() {
        QueueType queueType = new QueueType(FileExt.CSV, "2,-2,EUR,оплата заказа", 2, "src/test/resources/files/orders1.csv");
        csvParser.convertFromQueue(queueType, validator);
        String sout = out.toString();
        assertThat(sout, containsString("Сумма ордера меньше 0"));
    }

    @Test
    void convertFromQueueEmptyCurrency() {
        QueueType queueType = new QueueType(FileExt.CSV, "1,100,,оплата заказа", 2, "src/test/resources/files/orders1.csv");
        csvParser.convertFromQueue(queueType, validator);
        String sout = out.toString();
        assertThat(sout, containsString("Валюта суммы ордера пустая"));
    }

    @Test
    void convertFromQueueErrorCurrency() {
        QueueType queueType = new QueueType(FileExt.CSV, "1,100,US2D,оплата заказа", 2, "src/test/resources/files/orders1.csv");
        csvParser.convertFromQueue(queueType, validator);
        String sout = out.toString();
        assertThat(sout, containsString("Не удалось определить валюту суммы ордера"));
    }

    @Test
    void convertFromQueueEmptyComment() {
        QueueType queueType = new QueueType(FileExt.CSV, "1,100,USD,", 2, "src/test/resources/files/orders1.csv");
        csvParser.convertFromQueue(queueType, validator);
        String sout = out.toString();
        assertThat(sout, containsString("Комментарий по ордеру пустой"));
    }

}