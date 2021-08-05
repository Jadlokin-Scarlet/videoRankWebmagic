package com.tilitili.spider.service;

import com.tilitili.common.emnus.TaskStatus;
import com.tilitili.common.entity.message.TaskMessage;
import com.tilitili.common.mapper.TaskMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class JmsService {
    private final JmsTemplate jmsTemplate;
    private final TaskMapper taskMapper;

    @Autowired
    public JmsService(JmsTemplate jmsTemplate, TaskMapper taskMapper) {
        this.jmsTemplate = jmsTemplate;
        this.taskMapper = taskMapper;
    }

    public TaskMessage receiveAndConvert(String nameSpace) {
        TaskMessage taskMessage = (TaskMessage) jmsTemplate.receiveAndConvert(nameSpace);
        if (taskMessage == null) { return null; }
        taskMapper.updateStatusById(taskMessage.getId(), TaskStatus.WAIT.getValue(), TaskStatus.SPIDER.getValue());
        return taskMessage;
    }
}
