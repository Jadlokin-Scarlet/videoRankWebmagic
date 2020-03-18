package com.jadlokin.test.component;

import com.jadlokin.test.util.VideoSequence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Spider;

@Component
public class MainSpider {

	private VideoSequence videoSequence;

	@Autowired
	public MainSpider(VideoSequence sequence) {
		this.videoSequence = sequence;
	}

	@Bean
	public Spider spider(MainPageProcessor mainPageProcessor, MainPipeline mainPipeline) {
		return Spider.create(mainPageProcessor)
				.addPipeline(mainPipeline)
				.addUrl(videoSequence.lastVideo())
				.thread(1);
	}

}
