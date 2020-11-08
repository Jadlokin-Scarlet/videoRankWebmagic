package com.tilitili;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import us.codecraft.webmagic.Spider;

import javax.annotation.PostConstruct;

@Slf4j
@SpringBootApplication
@MapperScan("com.tilitili.common.mapper")
public class StartApplication {

	private final Spider spider;

	@Autowired
	public StartApplication(Spider viewSpider) {
		this.spider = viewSpider;
	}

	public static void main(String[] args) {
		SpringApplication.run(StartApplication.class, args);
	}


	@PostConstruct
	public void run() {
		spider.start();
	}

}
