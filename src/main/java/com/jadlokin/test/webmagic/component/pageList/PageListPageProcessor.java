package com.jadlokin.test.webmagic.component.pageList;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
public class PageListPageProcessor implements PageProcessor {

//	private VideoInfoMapper videoInfoMapper;

	@Override
	public void process(Page page) {
		String av = page.getUrl().get().split("=")[1];
		JSONObject obj = JSONObject.parseObject(page.getJson().get());
		JSONArray data = obj.getJSONArray("data");
		if (data != null) {
			Stream<VideoPage> videoPageStream = IntStream.range(0, data.size())
					.mapToObj(data::getJSONObject)
					.map(this::newVideoPage)
					.map(videoPage -> videoPage.setAv(Long.valueOf(av)));
			page.putField("videoPageStream", videoPageStream);
		}
	}

	@Override
	public Site getSite() {
		return Site.me().setRetryTimes(1).setSleepTime(5);
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
