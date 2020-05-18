package com.jadlokin.test.webmagic.component.view;


import com.jadlokin.test.webmagic.entity.VideoInfo;
import com.jadlokin.test.webmagic.mapper.AvMapper;
import com.jadlokin.test.webmagic.mapper.VideoInfoMapper;
import com.jadlokin.test.webmagic.mapper.VideoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.DuplicateRemovedScheduler;
import us.codecraft.webmagic.scheduler.MonitorableScheduler;

import java.util.LinkedList;
import java.util.Queue;

@Component
public class ViewScheduler extends DuplicateRemovedScheduler implements MonitorableScheduler {
	private AvMapper avMapper;
	private VideoInfoMapper videoInfoMapper;
	private Queue<Request> requestQueue = new LinkedList<>();

	@Autowired
	public ViewScheduler(VideoInfoMapper videoInfoMapper, VideoMapper videoMapper, AvMapper avMapper) {
		this.videoInfoMapper = videoInfoMapper;
		this.avMapper = avMapper;
		initQueue();
	}

	private void initQueue() {
		avMapper.selectAllAv().stream()
//				.limit(10)
				.map(av -> "https://api.bilibili.com/x/web-interface/view?aid=" + av)
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

	@Override
	public int getLeftRequestsCount(Task task) {
		return requestQueue.size();
	}

	@Override
	public int getTotalRequestsCount(Task task) {
		return 999999999;
	}
}
