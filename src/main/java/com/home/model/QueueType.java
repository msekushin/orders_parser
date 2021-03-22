package com.home.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
public class QueueType {

    private FileExt type;
    private String data;
    private long lineNumber;
    private String filename;

}
