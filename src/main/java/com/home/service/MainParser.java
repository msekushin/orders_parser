package com.home.service;

import com.home.model.FileExt;
import com.home.model.Order;
import com.home.model.QueueType;
import com.home.model.ThreadSafeQueue;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import javax.annotation.PostConstruct;
import javax.validation.Validation;
import javax.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

@Slf4j
public class MainParser {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private final Map<FileExt, FileParser> parsers;
    public ThreadSafeQueue<QueueType> threadQueue;
    @Value("${max-count-parser-thread}")
    private int maxCountParserThread;
    @Value("${max-count-converter-thread}")
    private int maxCountConverterThread;
    @Value("${queue.start-capacity}")
    private int capacity;
    @Value("${queue.get-timeout}")
    private long queueTimeOut;
    @Value("${count-down-latch-timeout}")
    private long countDownLatchTimeout;
    private String[] args;

    public MainParser(Map<FileExt, FileParser> parsers) {
        this.parsers = parsers;
    }

    @PostConstruct
    public void init() {
        threadQueue = new ThreadSafeQueue<>(capacity, queueTimeOut, TimeUnit.SECONDS);
    }

    public void setArgs(String[] sourceArgs) {
        this.args = sourceArgs;
    }

    public void parseFiles() {
        try {
            if (args.length > 0) {
                ThreadPoolExecutor parserExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(Math.min(args.length, maxCountParserThread));
                CountDownLatch fileCountDownLatch = new CountDownLatch(this.args.length);
                for (String file : this.args) {
                    if (file != null && !file.isEmpty()) {
                        parserExecutor.submit(() -> {
                            Order order = null;
                            try (BufferedReader br = Files.newBufferedReader(Paths.get(file))) {
                                String line;
                                FileExt fileExt = FileExt.valueOfByType(StringUtils.getFilenameExtension(file));
                                if (fileExt != null) {
                                    long currentLine = 0;
                                    while ((line = br.readLine()) != null) {
                                        threadQueue.put(new QueueType(fileExt, line, ++currentLine, file));
                                    }
                                } else {
                                    order = Order.builder()
                                                 .filename(file)
                                                 .result("Неизвестный тип расширения файла")
                                                 .build();
                                }
                            } catch (IOException e) {
                                log.error(e.getMessage(), e);
                                order = Order.builder()
                                             .filename(file)
                                             .result("Не удалось прочитать данный файл")
                                             .build();
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                log.error(e.getMessage(), e);
                                order = Order.builder()
                                             .filename(file)
                                             .result("При обработке файла произошла ошибка")
                                             .build();
                            } catch (Exception e) {
                                log.error(e.getMessage(), e);
                                order = Order.builder()
                                             .filename(file)
                                             .result("При обработке файла произошла ошибка")
                                             .build();
                            } finally {
                                fileCountDownLatch.countDown();
                            }
                            if (order != null) {
                                System.out.println(order.toString());
                            }
                        });
                    } else {
                        fileCountDownLatch.countDown();
                    }
                }
                boolean await = fileCountDownLatch.await(countDownLatchTimeout, TimeUnit.SECONDS);
                if (!await) {
                    log.warn("Превышено время ожидания чтения файлов. Чтение файлов остановлено.");
                }
                parserExecutor.shutdown();
            } else {
                log.warn("Empty input args");
            }
            threadQueue.complete();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error(e.getMessage(), e);
        }
    }

    public void convertData() {
        ThreadPoolExecutor converterExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(maxCountConverterThread);
        IntStream.range(0, maxCountConverterThread)
                 .forEach(idx -> converterExecutor.submit(() -> {
                     while (threadQueue.isContinue()) {
                         try {
                             QueueType queueType = threadQueue.get();
                             if (queueType != null) {
                                 FileExt type = queueType.getType();
                                 FileParser fileParser = parsers.get(type);
                                 fileParser.convertFromQueue(queueType, validator);
                             }
                         } catch (InterruptedException e) {
                             Thread.currentThread().interrupt();
                             log.error(e.getMessage(), e);
                         }
                     }
                 }));
        converterExecutor.shutdown();
    }
}
