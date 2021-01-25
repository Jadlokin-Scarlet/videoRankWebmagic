package com.tilitili.spider.component.tag;


import com.tilitili.common.emnus.TaskStatus;
import com.tilitili.common.entity.message.TaskMessage;
import com.tilitili.common.mapper.TaskMapper;
import com.tilitili.spider.util.Log;
import com.tilitili.spider.util.object.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.DuplicateRemovedScheduler;
import us.codecraft.webmagic.scheduler.MonitorableScheduler;

import java.time.Instant;
import java.util.LinkedList;
import java.util.Queue;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.tilitili.spider.util.BilibiliApi.getTagForVideoByAv;

@Component
public class TagScheduler extends DuplicateRemovedScheduler {
	private final TaskMapper taskMapper;
	private final JmsTemplate jmsTemplate;

	private final LinkedList<Request> requestLinkedList = new LinkedList<>();

	@Autowired
	public TagScheduler(TaskMapper taskMapper, JmsTemplate jmsTemplate) {
		this.taskMapper = taskMapper;
		this.jmsTemplate = jmsTemplate;
	}

//	private void initQueue() {
//		Stream.of(937, 2493141, 2485355)
//				.flatMap(this::getUrlListByTagId)
//				.map(Request::new)
//				.forEach(requestQueue::add);
//	}

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
		TaskMessage taskMessage = (TaskMessage) jmsTemplate.receiveAndConvert("SpiderVideoTagTaskMessage");
		if (taskMessage == null) {
			return null;
		}
		Log.info("receive spider video tag task: {}", taskMessage);
		taskMapper.updateStatusById(taskMessage.getId(), TaskStatus.WAIT.getValue(), TaskStatus.SPIDER.getValue());

		return new Request(getTagForVideoByAv(taskMessage.getValue(), taskMessage.getId()));
	}
//
//	private Stream<String> getUrlListByTagId(int tagId) {
//		return IntStream.range(1, 2)
//				.mapToObj(index -> getUrlByTagIdAndIndex(tagId, index));
//	}
//
//	private String getUrlByTagIdAndIndex(int tagId, int index) {
//		return "https://api.bilibili.com/x/tag/detail?ps=1"
//				+ "&time=" + Instant.now()
//				+ "&tag_id=" + tagId
//				+ "&pn=" + index;
//	}
//
//	@Override
//	public int getLeftRequestsCount(Task task) {
//		return requestQueue.size();
//	}
//
//	@Override
//	public int getTotalRequestsCount(Task task) {
//		return 3000;
//	}
}
