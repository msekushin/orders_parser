package com.home.configuration;

import com.home.model.FileExt;
import com.home.service.FileParser;
import com.home.service.MainParser;
import com.home.service.impl.CSVParserImpl;
import com.home.service.impl.JSONParserImpl;
import java.util.EnumMap;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.home.model.FileExt.CSV;
import static com.home.model.FileExt.JSON;

@Configuration
public class Config {

    @Bean
    public CSVParserImpl csvParser() {
        return new CSVParserImpl();
    }

    @Bean
    public JSONParserImpl jsonParser() {
        return new JSONParserImpl();
    }

    @Bean
    public MainParser mainParser(
            CSVParserImpl csvParser,
            JSONParserImpl jsonParser
    ) {
        Map<FileExt, FileParser> threadMaps = new EnumMap<>(FileExt.class);
        threadMaps.put(CSV, csvParser);
        threadMaps.put(JSON, jsonParser);
        return new MainParser(threadMaps);
    }
}
