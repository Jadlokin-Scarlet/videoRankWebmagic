package com.jadlokin.test.webmagic.component;

import com.jadlokin.test.webmagic.util.VideoSequence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.scheduler.RedisScheduler;

@Configuration
@PropertySource("application.properties")
public class MainSpider {
	private RedisTemplate redisTemplate;
	private VideoSequence videoSequence;

	@Value("${spring.redis.host}")
	private String host;

	@Autowired
	public MainSpider(VideoSequence sequence) {
		this.videoSequence = sequence;
	}

	@Bean
	public RedisScheduler redisScheduler() {
		return new RedisScheduler(host);
	}

	@Bean
	public Spider spider(RedisScheduler redisScheduler, MainPageProcessor mainPageProcessor, MainPipeline mainPipeline) {
		return Spider.create(mainPageProcessor)
				.addPipeline(mainPipeline)
				.setScheduler(redisScheduler)
				.addUrl(videoSequence.lastVideo())
				.thread(1);
	}

}
