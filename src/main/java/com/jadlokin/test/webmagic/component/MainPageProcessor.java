package com.jadlokin.test.webmagic.component;

import com.alibaba.fastjson.JSONObject;
import com.jadlokin.test.webmagic.entity.VideoInfo;
import com.jadlokin.test.webmagic.util.VideoSequence;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

@Slf4j
@Component
public class MainPageProcessor implements PageProcessor {

	private VideoSequence videoSequence;

	@Autowired
	public MainPageProcessor(VideoSequence sequence) {
		this.videoSequence = sequence;
	}

	@Override
	public void process(Page page) {
		JSONObject obj = JSONObject.parseObject(page.getJson().get());
		if (!Arrays.asList("-404", "62002").contains(obj.getString("code"))) {
			String av = page.getUrl().get().split("=")[1];
			log.warn("av" + av + " is found");
//			page.putField("data", obj.getJSONObject("data"));
		} else {
//			String av = page.getUrl().get().split("=")[1];
//			log.warn("av" + av + " is not found");
		}

		if(videoSequence.hasLast()) {
			page.addTargetRequest(videoSequence.lastVideo());
		}
	}

	@Override
	public Site getSite() {
		return Site.me().setRetryTimes(1).setSleepTime(5);
	}

}
