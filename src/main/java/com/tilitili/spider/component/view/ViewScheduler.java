package com.tilitili.spider.component.view;


import com.tilitili.common.emnus.TaskStatus;
import com.tilitili.common.entity.message.TaskMessage;
import com.tilitili.common.mapper.TaskMapper;
import com.tilitili.common.utils.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.scheduler.DuplicateRemovedScheduler;

import java.util.LinkedList;

@Component
public class ViewScheduler extends DuplicateRemovedScheduler {
	private final String getVideoByAV = "https://api.bilibili.com/x/web-interface/view?aid=%s&_id_=%s";

	private final TaskMapper taskMapper;
	private final JmsTemplate jmsTemplate;

	private final LinkedList<Request> requestLinkedList = new LinkedList<>();

	@Autowired
	public ViewScheduler(TaskMapper taskMapper, JmsTemplate jmsTemplate) {
		this.taskMapper = taskMapper;
		this.jmsTemplate = jmsTemplate;
	}

	@Override
	public void push(Request request, us.codecraft.webmagic.Task task) {
		requestLinkedList.addFirst(request);
	}

	@Override
	public Request poll(us.codecraft.webmagic.Task task) {
		if (!requestLinkedList.isEmpty()) {
			Request request = requestLinkedList.poll();
			long id = Long.parseLong(request.getUrl().split("&_id_=")[1]);
			taskMapper.updateStatusById(id, TaskStatus.WAIT.getValue(), TaskStatus.SPIDER.getValue());
			return requestLinkedList.poll();
		}
		TaskMessage taskMessage = (TaskMessage) jmsTemplate.receiveAndConvert("SpiderVideoViewTaskMessage");
		if (taskMessage == null) {
			return null;
		}
		Log.info("receive spider video view task: {}", taskMessage);
		taskMapper.updateStatusById(taskMessage.getId(), TaskStatus.WAIT.getValue(), TaskStatus.SPIDER.getValue());

		return new Request(String.format(getVideoByAV, taskMessage.getValue(), taskMessage.getId()));
	}
}
