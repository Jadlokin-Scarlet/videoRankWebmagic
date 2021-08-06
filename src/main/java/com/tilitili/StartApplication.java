package com.tilitili;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import us.codecraft.webmagic.Spider;

import javax.annotation.PostConstruct;
import java.util.List;

@Slf4j
@SpringBootApplication
@MapperScan("com.tilitili.common.mapper")
public class StartApplication {

	private final List<Spider> spiderList;
	private final Spider viewSpider;
	private final Environment environment;

	@Autowired
	public StartApplication(List<Spider> spiderList, Spider viewSpider, Environment environment) {
		this.spiderList = spiderList;
		this.viewSpider = viewSpider;
		this.environment = environment;
	}

	public static void main(String[] args) {
		SpringApplication.run(StartApplication.class, args);
	}


	@PostConstruct
	public void run() {
		log.info(environment.getProperty("ip"));
		spiderList.forEach(Spider::start);
	}

	@PostConstruct
	public void test() {
//		viewSpider.start();
	}

}
