package com.tilitili.spider.component.owner;


import com.tilitili.common.emnus.TaskStatus;
import com.tilitili.common.entity.message.TaskMessage;
import com.tilitili.common.mapper.TaskMapper;
import com.tilitili.spider.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.DuplicateRemovedScheduler;

import java.util.LinkedList;

import static com.tilitili.spider.util.BilibiliApi.getOwnerByUid;

@Component
public class OwnerScheduler extends DuplicateRemovedScheduler {
	private final TaskMapper taskMapper;
	private final JmsTemplate jmsTemplate;

	private final LinkedList<Request> requestLinkedList = new LinkedList<>();

	@Autowired
	public OwnerScheduler(TaskMapper taskMapper, JmsTemplate jmsTemplate) {
		this.taskMapper = taskMapper;
		this.jmsTemplate = jmsTemplate;
	}

	@Override
	public void push(Request request, Task task) {
		requestLinkedList.addFirst(request);
	}

	@Override
	public Request poll(Task task) {
		if (!requestLinkedList.isEmpty()) {
			Request request = requestLinkedList.poll();
			long id = Long.parseLong(request.getUrl().split("&_id_=")[1]);
			taskMapper.updateStatusById(id, TaskStatus.WAIT.getValue(), TaskStatus.SPIDER.getValue());
			return requestLinkedList.poll();
		}
		TaskMessage taskMessage = (TaskMessage) jmsTemplate.receiveAndConvert("SpiderOwnerTaskMessage");
		if (taskMessage == null) {
			return null;
		}
		Log.info("receive spider video owner task: {}", taskMessage);
		taskMapper.updateStatusById(taskMessage.getId(), TaskStatus.WAIT.getValue(), TaskStatus.SPIDER.getValue());

		return new Request(getOwnerByUid(taskMessage.getValue(), taskMessage.getId()));
	}

}
