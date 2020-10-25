package com.tilitili.spider.component.tag;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import us.codecraft.webmagic.Spider;

@Configuration
public class TagSpiderConfiguration {
	@Bean
	public Spider tagSpider(TagScheduler tagScheduler, TagPageProcessor tagPageProcessor, TagPipeline tagPipeline) {
		return Spider.create(tagPageProcessor)
				.addPipeline(tagPipeline)
				.setScheduler(tagScheduler)
				.thread(1);
	}

}
