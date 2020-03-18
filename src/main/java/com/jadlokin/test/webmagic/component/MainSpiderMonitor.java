package com.jadlokin.test.webmagic.component;

import org.springframework.context.annotation.Bean;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.monitor.SpiderMonitor;

import javax.management.JMException;

public class MainSpiderMonitor {

	@Bean
	public SpiderMonitor spiderMonitor(Spider spider) throws JMException {
		return SpiderMonitor.instance().register(spider);
	}

}
