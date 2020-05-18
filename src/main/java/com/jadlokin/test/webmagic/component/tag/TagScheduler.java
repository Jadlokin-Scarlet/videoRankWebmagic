package com.jadlokin.test.webmagic.component.tag;


import com.jadlokin.test.webmagic.util.object.Timer;
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

@Component
public class TagScheduler extends DuplicateRemovedScheduler implements MonitorableScheduler {

	private Queue<Request> requestQueue = new LinkedList<>();

	public TagScheduler() {
		new Timer(this::initQueue, 10 * 1000);
	}

	private void initQueue() {
		Stream.of(937, 2493141, 2485355)
				.flatMap(this::getUrlListByTagId)
				.map(Request::new)
				.forEach(requestQueue::add);
	}

	@Override
	public void push(Request request, Task task) {
		requestQueue.add(request);
	}

	@Override
	public Request poll(Task task) {
		return requestQueue.poll();
	}

	private Stream<String> getUrlListByTagId(int tagId) {
		return IntStream.range(1, 2)
				.mapToObj(index -> getUrlByTagIdAndIndex(tagId, index));
	}

	private String getUrlByTagIdAndIndex(int tagId, int index) {
		return "https://api.bilibili.com/x/tag/detail?ps=1"
				+ "&time=" + Instant.now()
				+ "&tag_id=" + tagId
				+ "&pn=" + index;
	}

	@Override
	public int getLeftRequestsCount(Task task) {
		return requestQueue.size();
	}

	@Override
	public int getTotalRequestsCount(Task task) {
		return 3000;
	}
}
