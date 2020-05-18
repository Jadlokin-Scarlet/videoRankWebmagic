package com.jadlokin.test.webmagic.component.view;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jadlokin.test.webmagic.entity.VideoInfo;
import com.jadlokin.test.webmagic.entity.VideoPage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.stream.IntStream;
import java.util.stream.Stream;

@Slf4j
@Component
public class ViewPageProcessor implements PageProcessor {

//	private VideoInfoMapper videoInfoMapper;

	@Override
	public void process(Page page) {
		String av = page.getUrl().get().split("=")[1];
		JSONObject rep = JSONObject.parseObject(page.getJson().get());
		page.putField("av", av);
		page.putField("rep", rep);
//		Integer code = obj.getInteger("code");
//		if (code.equals(404)) {
//			VideoInfo videoInfo = new VideoInfo()
//					.setAv(Long.valueOf(av))
//					.setIsDelete(true);
//			page.putField("videoInfo", videoInfo);
//		}
	}

	@Override
	public Site getSite() {
		return Site.me().setRetryTimes(1).setSleepTime(50);
	}

	private VideoPage newVideoPage(JSONObject obj) {
		return new VideoPage()
				.setCid(obj.getLong("cid"))
				.setPage(obj.getInteger("page"))
				.setFrom(obj.getString("from"))
				.setPart(obj.getString("part"))
				.setDuration(obj.getLong("duration"));
	}
}
