package com.tilitili.spider.component.pageList;


import com.tilitili.common.entity.VideoInfo;
import com.tilitili.common.mapper.VideoInfoMapper;
import com.tilitili.common.mapper.VideoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.DuplicateRemovedScheduler;
import us.codecraft.webmagic.scheduler.MonitorableScheduler;

import java.util.LinkedList;
import java.util.Queue;

@Component
public class PageListScheduler extends DuplicateRemovedScheduler implements MonitorableScheduler {
	private VideoInfoMapper videoInfoMapper;
	private VideoMapper videoMapper;
	private Queue<Request> requestQueue = new LinkedList<>();

	@Autowired
	public PageListScheduler(VideoInfoMapper videoInfoMapper, VideoMapper videoMapper) {
		this.videoInfoMapper = videoInfoMapper;
		this.videoMapper = videoMapper;
		initQueue();
	}

	private void initQueue() {
//		videoInfoMapper.selectAll().stream()
//		videoMapper.selectNoPageVideo().stream()
//				.map(av -> "https://api.bilibili.com/x/player/pagelist?aid=" + av)
//				.map(Request::new)
//				.forEach(requestQueue::add);
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
