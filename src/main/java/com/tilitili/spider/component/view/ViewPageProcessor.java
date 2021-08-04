package com.tilitili.spider.component.view;

import com.alibaba.fastjson.JSONObject;
import com.tilitili.common.entity.VideoInfo;
import com.tilitili.common.entity.VideoPage;
import com.tilitili.spider.util.QQUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.Arrays;
import java.util.HashSet;

@Slf4j
@Component
public class ViewPageProcessor implements PageProcessor {
	private final QQUtil qqUtil;

	@Value("${spider.wait-time}")
	private Integer waitTime;

	@Autowired
	public ViewPageProcessor(QQUtil qqUtil) {
		this.qqUtil = qqUtil;
	}

	@Override
	public void process(Page page) {
		page.getStatusCode();
		String id = page.getUrl().get().split("&_id_=")[1];
		String av = page.getUrl().get().split("&_id_=")[0].split("aid=")[1];
		JSONObject rep = JSONObject.parseObject(page.getJson().get());
		Integer code = rep.getInteger("code");
		if (code.equals(-412) || code.equals(-504)) {
			log.error(rep.toString());
			qqUtil.sendRiskError(this.getClass());
			try {
				Thread.sleep(waitTime * 60 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			page.addTargetRequest(page.getUrl().get());
			page.setSkip(true);
		}else {
			page.putField("id", id);
			page.putField("av", av);
			page.putField("rep", rep);
		}
	}

	@Override
	public Site getSite() {
		return Site.me().setRetryTimes(10 * 1000).setSleepTime(10 * 1000)
				.setCharset("UTF-8")
				.setAcceptStatCode(new HashSet<>(
						Arrays.asList(412, 200)
				));
	}
}
