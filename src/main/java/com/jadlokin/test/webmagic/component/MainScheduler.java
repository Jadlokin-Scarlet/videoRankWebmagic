package com.jadlokin.test.webmagic.component;


import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.Scheduler;
@Component
public class MainScheduler implements Scheduler {
	@Override
	public void push(Request request, Task task) {

	}

	@Override
	public Request poll(Task task) {
		return null;
	}
}
