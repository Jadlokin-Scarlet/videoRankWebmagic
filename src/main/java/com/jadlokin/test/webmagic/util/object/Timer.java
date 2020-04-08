package com.jadlokin.test.webmagic.util.object;

public class Timer extends java.util.Timer{
	public Timer(Runnable task, long period) {
		super.schedule(new TimerTask(task), (long) 0, period);
	}

}
