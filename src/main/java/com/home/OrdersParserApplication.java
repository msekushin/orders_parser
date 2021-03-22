package com.home;

import com.home.service.MainParser;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class OrdersParserApplication implements ApplicationRunner {

    @Autowired
    MainParser mainParser;

    public static void main(String[] args) {
        SpringApplication.run(OrdersParserApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {
        String[] sourceArgs = args.getSourceArgs();
        if (sourceArgs.length > 0) {
            mainParser.setArgs(sourceArgs);
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> mainParser.convertData());
            mainParser.parseFiles();
            executor.shutdown();
        } else {
            log.warn("Empty input args");
        }
    }
}
