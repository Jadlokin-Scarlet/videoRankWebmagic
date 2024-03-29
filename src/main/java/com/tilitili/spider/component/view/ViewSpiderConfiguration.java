package com.tilitili.spider.component.view;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import us.codecraft.webmagic.Spider;

@Configuration
public class ViewSpiderConfiguration {

//	@Bean
	public Spider viewSpider(ViewPageProcessor viewPageProcessor, ViewPipeline viewPipeline, ViewScheduler viewScheduler) {
		return Spider.create(viewPageProcessor)
				.addPipeline(viewPipeline)
				.setScheduler(viewScheduler)
				.setExitWhenComplete(false)
				.thread(1);
	}

}
