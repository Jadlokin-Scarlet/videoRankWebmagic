package com.tilitili;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import us.codecraft.webmagic.Spider;

import javax.annotation.PostConstruct;
import java.util.List;

@Slf4j
@SpringBootApplication
@MapperScan("com.tilitili.common.mapper")
public class StartApplication {

	private final List<Spider> spiderList;
	private final Spider ownerSpider;

	@Autowired
	public StartApplication(List<Spider> spiderList, Spider ownerSpider) {
		this.spiderList = spiderList;
		this.ownerSpider = ownerSpider;
	}

	public static void main(String[] args) {
		SpringApplication.run(StartApplication.class, args);
	}


	@PostConstruct
	public void run() {
		spiderList.forEach(Spider::start);
	}

	@PostConstruct
	public void test() {
//		ownerSpider.start();
	}

}
