package com.home.service;

import com.home.model.FileExt;
import com.home.model.QueueType;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
class MainParserTest {

    @Autowired
    private MainParser mainParser;

    @BeforeEach
    public void initBeforeEach() {
        mainParser.init();
    }

    @Test
    void parseFilesSuccess() {
        mainParser.setArgs(new String[]{"src/test/resources/files/orders1.csv", "src/test/resources/files/orders2.json"});
        assertDoesNotThrow(this::startParseFileAndWaitThreadQueueIsContinue);
        assertEquals(16, mainParser.threadQueue.size());
    }

    @Test
    void parseEmptyFiles() {
        mainParser.setArgs(new String[]{"src/test/resources/files/orders3.csv"});
        assertDoesNotThrow(this::startParseFileAndWaitThreadQueueIsContinue);
        assertEquals(0, mainParser.threadQueue.size());
    }

    @Test
    void parseWrongFiles() {
        mainParser.setArgs(new String[]{"src/test/resources/files/notfound.csv"});
        assertDoesNotThrow(this::startParseFileAndWaitThreadQueueIsContinue);
        assertEquals(0, mainParser.threadQueue.size());
    }

    @SneakyThrows
    @Test
    void convertData() {
        mainParser.threadQueue.put(new QueueType(FileExt.CSV, "1,100,USD,оплата заказа", 1, "src/test/resources/files/orders1.csv"));
        mainParser.threadQueue.put(new QueueType(FileExt.CSV, "2,,EUR,оплата заказа", 2, "src/test/resources/files/orders1.csv"));
        mainParser.threadQueue.put(new QueueType(FileExt.CSV, "3,100,USD,", 3, "src/test/resources/files/orders1.csv"));
        mainParser.threadQueue.put(new QueueType(FileExt.CSV, "\"asd\",\"sad\",1,13213 123123", 4, "src/test/resources/files/orders1.csv"));
        startConvertFromQueueAndWaitThreadQueueIsContinue();
        assertEquals(0, mainParser.threadQueue.size());
    }

    private void startParseFileAndWaitThreadQueueIsContinue() throws TimeoutException {
        ExecutorService parseFilesExecutor = Executors.newSingleThreadExecutor();
        parseFilesExecutor.execute(() -> mainParser.parseFiles());
        parseFilesExecutor.shutdown();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future future = executor.submit(() -> {
            while (mainParser.threadQueue.getContinueWork()) {
            }
        });
        try {
            future.get(30, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            executor.shutdownNow();
        }
    }
    private void startConvertFromQueueAndWaitThreadQueueIsContinue() throws TimeoutException {
        ExecutorService convertQueueExecutor = Executors.newSingleThreadExecutor();
        convertQueueExecutor.execute(() -> mainParser.convertData());
        convertQueueExecutor.shutdown();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future future = executor.submit(() -> {
            while (!mainParser.threadQueue.isEmpty()) {
            }
        });
        try {
            future.get(30, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            executor.shutdownNow();
        }
    }

}