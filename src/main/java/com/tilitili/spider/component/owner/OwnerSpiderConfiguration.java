package com.tilitili.spider.component.owner;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import us.codecraft.webmagic.Spider;

@Configuration
public class OwnerSpiderConfiguration {
	@Bean
	public Spider ownerSpider(OwnerScheduler ownerScheduler, OwnerPageProcessor ownerPageProcessor, OwnerPipeline ownerPipeline) {
		return Spider.create(ownerPageProcessor)
				.addPipeline(ownerPipeline)
				.setScheduler(ownerScheduler)
				.setExitWhenComplete(false)
				.thread(1);
	}

}
