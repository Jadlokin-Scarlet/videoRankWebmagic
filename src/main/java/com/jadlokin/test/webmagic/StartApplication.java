package com.jadlokin.test.webmagic;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import us.codecraft.webmagic.Spider;

import javax.annotation.PostConstruct;
import com.jadlokin.test.webmagic.util.object.Timer;

import java.util.ArrayList;

@Slf4j
@SpringBootApplication
@MapperScan("com.jadlokin.test.webmagic.mapper")
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
		new Timer(spider::start, 60 * 60 * 1000);
	}

}
