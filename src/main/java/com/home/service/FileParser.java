package com.home.service;

import com.home.model.QueueType;
import javax.validation.Validator;

public interface FileParser {

    void convertFromQueue(QueueType queueType, Validator validator);

}
