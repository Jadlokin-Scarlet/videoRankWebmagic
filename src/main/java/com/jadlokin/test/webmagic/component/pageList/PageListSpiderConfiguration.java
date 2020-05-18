package com.jadlokin.test.webmagic.component.pageList;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import us.codecraft.webmagic.Spider;

@Configuration
public class PageListSpiderConfiguration {

	@Bean
	public Spider pageListSpider(PageListPageProcessor pageVideoPageProcessor, PageListPipeline pageVideoPipeline, PageListScheduler pageVideoScheduler) {
		return Spider.create(pageVideoPageProcessor)
				.addPipeline(pageVideoPipeline)
				.setScheduler(pageVideoScheduler)
				.thread(1);
	}

}
