package com.jadlokin.test.webmagic;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import us.codecraft.webmagic.Spider;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.time.ZoneId;

@Slf4j
@SpringBootApplication
@MapperScan("com.jadlokin.test.mapper")
public class StartApplication {

	private final Spider spider;

	@Autowired
	public StartApplication(Spider spider) {
		this.spider = spider;
	}

	public static void main(String[] args) {
		SpringApplication.run(StartApplication.class, args);
	}


	@PostConstruct
	public void run() {
		log.warn(Instant.now().atZone(ZoneId.systemDefault()).toString());
		spider.start();
		log.warn(Instant.now().atZone(ZoneId.systemDefault()).toString());
//		com.microsoft.sqlserver.jdbc.SQLServerDriver
	}

}
