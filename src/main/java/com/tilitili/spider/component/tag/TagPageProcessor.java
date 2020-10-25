package com.tilitili.spider.component.tag;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

@Slf4j
@Component
public class TagPageProcessor implements PageProcessor {

//	private VideoInfoMapper videoInfoMapper;

	@Override
	public void process(Page page) {
		JSONObject obj = JSONObject.parseObject(page.getJson().get());
		JSONObject video = obj.getJSONObject("data")
				.getJSONObject("news")
				.getJSONArray("archives")
				.getJSONObject(0);
		page.putField("data", video);
	}

	@Override
	public Site getSite() {
		return Site.me().setRetryTimes(1).setSleepTime(5);
	}

}
