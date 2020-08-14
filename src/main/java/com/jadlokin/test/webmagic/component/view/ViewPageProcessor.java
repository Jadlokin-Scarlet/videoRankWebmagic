package com.jadlokin.test.webmagic.component.view;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jadlokin.test.webmagic.entity.VideoInfo;
import com.jadlokin.test.webmagic.entity.VideoPage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
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
		Integer code = rep.getInteger("code");
		if (code.equals(-412)) {
			try {
				Thread.sleep(10 * 60 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			page.addTargetRequest(page.getUrl().get());
		}else {
			page.putField("av", av);
			page.putField("rep", rep);
		}

//			VideoInfo videoInfo = new VideoInfo()
//					.setAv(Long.valueOf(av))
//					.setIsDelete(true);
//			page.putField("videoInfo", videoInfo);
//		}
	}

	@Override
	public Site getSite() {
		return Site.me().setRetryTimes(1000).setSleepTime(1000)
				.setCharset("UTF-8")
				.setAcceptStatCode(new HashSet<>(
						Arrays.asList(412, 200)
				));
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
