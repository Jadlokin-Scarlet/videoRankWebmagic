package com.tilitili.spider.receiver;

import com.google.gson.GsonBuilder;
import com.tilitili.common.emnus.TaskStatus;
import com.tilitili.common.entity.Task;
import com.tilitili.common.entity.message.TaskMessage;
import com.tilitili.common.mapper.TaskMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TaskReceiver {
    private final TaskMapper taskMapper;

    @Autowired
    public TaskReceiver(TaskMapper taskMapper) {
        this.taskMapper = taskMapper;
    }

    @JmsListener(destination = "SpiderVideoTaskMessage")
    public void receiverSpiderVideoTask(TaskMessage taskMessage) {
        log.info(new GsonBuilder().setPrettyPrinting().create().toJson(taskMessage));
        taskMapper.update(new Task().setId(taskMessage.getId()).setStatus(TaskStatus.TIMEOUT.getValue()));
    }
}
