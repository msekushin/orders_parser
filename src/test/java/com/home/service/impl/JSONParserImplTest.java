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
class JSONParserImplTest {

    private ByteArrayOutputStream out;
    private JSONParserImpl jsonParser;
    private Validator validator;

    @BeforeAll
    public void initialize() {
        jsonParser = new JSONParserImpl();
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @BeforeEach
    public void initBeforeEach() {
        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
    }

    @Test
    void convertFromQueueSuccess() {
        QueueType queueType = new QueueType(FileExt.JSON, "{\"orderId\":2,\"amount\":1.23,\"currency\":\"USD\",\"comment\":\"оплата заказа\"}", 1,
                "src/test/resources/files/orders2.json");
        jsonParser.convertFromQueue(queueType, validator);
        String sout = out.toString();
        assertThat(sout, containsString("\"result\":\"OK\""));
    }

    @Test
    void convertFromQueueAllRequiredEmpty() {
        QueueType queueType = new QueueType(FileExt.JSON, "{\"orderId\":\"\",\"amount\":\"\",\"currency\":\"\",\"comment\":\"\"}", 1,
                "src/test/resources/files/orders2.json");
        jsonParser.convertFromQueue(queueType, validator);
        String sout = out.toString();
        assertThat(sout, containsString("Неверный формат идентификатора ордера"));
        assertThat(sout, containsString("Неверный формат суммы ордера"));
        assertThat(sout, containsString("Сумма ордера пустая"));
        assertThat(sout, containsString("Валюта суммы ордера пустая"));
        assertThat(sout, containsString("Идентификатор ордера пустой"));
        assertThat(sout, containsString("Комментарий по ордеру пустой"));
    }


    @Test
    void convertFromQueueNullOrderId() {
        QueueType queueType = new QueueType(FileExt.JSON, "{\"orderId\":null,\"amount\":1.23,\"currency\":\"USD\",\"comment\":\"оплата заказа\"}", 1,
                "src/test/resources/files/orders2.json");
        jsonParser.convertFromQueue(queueType, validator);
        String sout = out.toString();
        assertThat(sout, containsString("Неверный формат идентификатора ордера, Идентификатор ордера пустой"));
    }

    @Test
    void convertFromQueueErrorOrderId() {
        QueueType queueType = new QueueType(FileExt.JSON, "{\"orderId\":\"asdasd\",\"amount\":1.23,\"currency\":\"USD\",\"comment\":\"оплата заказа\"}", 1,
                "src/test/resources/files/orders2.json");
        jsonParser.convertFromQueue(queueType, validator);
        String sout = out.toString();
        assertThat(sout, containsString("Неверный формат идентификатора ордера"));
    }

    @Test
    void convertFromQueueNegativeOrderId() {
        QueueType queueType = new QueueType(FileExt.JSON, "{\"orderId\":-2,\"amount\":1.23,\"currency\":\"USD\",\"comment\":\"оплата заказа\"}", 1,
                "src/test/resources/files/orders2.json");
        jsonParser.convertFromQueue(queueType, validator);
        String sout = out.toString();
        assertThat(sout, containsString("Идентификатор ордера меньше 0"));
    }

    @Test
    void convertFromQueueNullAmount() {
        QueueType queueType = new QueueType(FileExt.JSON, "{\"orderId\":1,\"amount\":null,\"currency\":\"USD\",\"comment\":\"оплата заказа\"}", 1,
                "src/test/resources/files/orders2.json");
        jsonParser.convertFromQueue(queueType, validator);
        String sout = out.toString();
        assertThat(sout, containsString("Неверный формат суммы ордера, Сумма ордера пустая"));
    }

    @Test
    void convertFromQueueErrorAmount() {
        QueueType queueType = new QueueType(FileExt.JSON, "{\"orderId\":1,\"amount\":\"asd\",\"currency\":\"USD\",\"comment\":\"оплата заказа\"}", 1,
                "src/test/resources/files/orders2.json");
        jsonParser.convertFromQueue(queueType, validator);
        String sout = out.toString();
        assertThat(sout, containsString("Неверный формат суммы ордера, Сумма ордера пустая"));
    }

    @Test
    void convertFromQueueNegativeAmount() {
        QueueType queueType = new QueueType(FileExt.JSON, "{\"orderId\":1,\"amount\":-2,\"currency\":\"USD\",\"comment\":\"оплата заказа\"}", 1,
                "src/test/resources/files/orders2.json");
        jsonParser.convertFromQueue(queueType, validator);
        String sout = out.toString();
        assertThat(sout, containsString("Сумма ордера меньше 0"));
    }

    @Test
    void convertFromQueueNullCurrency() {
        QueueType queueType = new QueueType(FileExt.JSON, "{\"orderId\":1,\"amount\":12,\"currency\":null,\"comment\":\"оплата заказа\"}", 1,
                "src/test/resources/files/orders2.json");
        jsonParser.convertFromQueue(queueType, validator);
        String sout = out.toString();
        assertThat(sout, containsString("Не удалось определить валюту суммы ордера, Валюта суммы ордера пустая"));
    }

    @Test
    void convertFromQueueErrorCurrency() {
        QueueType queueType = new QueueType(FileExt.JSON, "{\"orderId\":1,\"amount\":12,\"currency\":\"Ua2SD\",\"comment\":\"оплата заказа\"}", 1,
                "src/test/resources/files/orders2.json");
        jsonParser.convertFromQueue(queueType, validator);
        String sout = out.toString();
        assertThat(sout, containsString("Не удалось определить валюту суммы ордера, Валюта суммы ордера пустая"));
    }

    @Test
    void convertFromQueueNullComment() {
        QueueType queueType = new QueueType(FileExt.JSON, "{\"orderId\":1,\"amount\":12,\"currency\":\"USD\",\"comment\":null}", 1,
                "src/test/resources/files/orders2.json");
        jsonParser.convertFromQueue(queueType, validator);
        String sout = out.toString();
        assertThat(sout, containsString("Не удалось определить комментарий по ордеру, Комментарий по ордеру пустой"));
    }

    @Test
    void convertFromQueueErrorComment() {
        QueueType queueType = new QueueType(FileExt.JSON, "{\"orderId\":1,\"amount\":12,\"currency\":\"USD\",\"comment\":123}", 1,
                "src/test/resources/files/orders2.json");
        jsonParser.convertFromQueue(queueType, validator);
        String sout = out.toString();
        assertThat(sout, containsString("Не удалось определить комментарий по ордеру, Комментарий по ордеру пустой"));
    }


}