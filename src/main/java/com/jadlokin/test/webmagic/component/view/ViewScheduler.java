package com.jadlokin.test.webmagic.component.view;


import com.jadlokin.test.webmagic.mapper.AvMapper;
import com.jadlokin.test.webmagic.mapper.VideoInfoMapper;
import com.jadlokin.test.webmagic.mapper.VideoMapper;
import com.jadlokin.test.webmagic.util.object.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.DuplicateRemovedScheduler;
import us.codecraft.webmagic.scheduler.MonitorableScheduler;

import java.util.LinkedList;
import java.util.TimerTask;

@Component
public class ViewScheduler extends DuplicateRemovedScheduler implements MonitorableScheduler {
	private AvMapper avMapper;
	private VideoInfoMapper videoInfoMapper;
	private LinkedList<Request> requestLinkedList = new LinkedList<>();

	@Autowired
	public ViewScheduler(VideoInfoMapper videoInfoMapper, VideoMapper videoMapper, AvMapper avMapper) {
		this.videoInfoMapper = videoInfoMapper;
		this.avMapper = avMapper;
		new Timer(this::initQueue, 1000 * 60 * 60 * 12);
		initQueue();
	}

	private void initQueue() {
		avMapper.selectOtherAv().stream()
//				.limit(10)
				.map(av -> "https://api.bilibili.com/x/web-interface/view?aid=" + av)
				.map(Request::new)
				.forEach(requestLinkedList::add);
	}

	@Override
	public void push(Request request, Task task) {
		requestLinkedList.addFirst(request);
	}

	@Override
	public Request poll(Task task) {
		return requestLinkedList.poll();
	}

	@Override
	public int getLeftRequestsCount(Task task) {
		return requestLinkedList.size();
	}

	@Override
	public int getTotalRequestsCount(Task task) {
		return 999999999;
	}
}
