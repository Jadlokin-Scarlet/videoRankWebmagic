package com.jadlokin.test.webmagic.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "api/video",produces = "application/json")
@Validated
@Slf4j
public class VideoController {

	@GetMapping("/{av}")
	public void getVideo(@PathVariable long av) {

	}

}
