package com.jadlokin.test.webmagic.component.pageList;


import com.jadlokin.test.webmagic.entity.VideoInfo;
import com.jadlokin.test.webmagic.mapper.VideoInfoMapper;
import com.jadlokin.test.webmagic.mapper.VideoMapper;
import com.jadlokin.test.webmagic.util.object.Timer;
import org.springframework.beans.factory.annotation.Autowired;
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
		videoMapper.selectNoPageVideo().stream()
//				.limit(10)
//				.map(VideoInfo::getAv)
				.map(av -> "https://api.bilibili.com/x/player/pagelist?aid=" + av)
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
