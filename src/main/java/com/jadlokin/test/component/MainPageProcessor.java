package com.jadlokin.test.component;

import com.alibaba.fastjson.JSONObject;
import com.jadlokin.test.entity.VideoInfo;
import com.jadlokin.test.util.VideoSequence;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
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
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		JSONObject obj = JSONObject.parseObject(page.getJson().get());
		if (!Arrays.asList("-404", "62002").contains(obj.getString("code"))) {
			JSONObject data = obj.getJSONObject("data");

			VideoInfo videoInfo = new VideoInfo()
					.setAv(data.getLong("aid"))
					.setName(data.getString("title"))
					.setImg(data.getString("pic"))
					.setType(data.getString("tname"))
					.setOwner(data.getJSONObject("owner").getString("name"))
					.setCopyright(data.getInteger("copyright") - 1 == 1)
					.setPubTime(formatter.format(new Date(data.getLong("pubdate"))));
			page.putField("videoInfo", videoInfo);
		} else {
			String av = page.getUrl().get().split("=")[1];
			log.warn("av" + av + " is not found");
		}

		if(videoSequence.hasLast()) {
			page.addTargetRequest(videoSequence.lastVideo());
		}
	}

	@Override
	public Site getSite() {
		return Site.me().setRetryTimes(1).setSleepTime(1);
	}

}
