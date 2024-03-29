package com.tilitili.spider.component.tag;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Sets;
import com.tilitili.spider.util.QQUtil;
import com.tilitili.common.entity.view.BaseView;
import com.tilitili.common.entity.view.tag.TagView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class TagPageProcessor implements PageProcessor {
	private final QQUtil qqUtil;

	@Value("${spider.wait-time}")
	private Integer waitTime;

	@Autowired
	public TagPageProcessor(QQUtil qqUtil) {
		this.qqUtil = qqUtil;
	}

	@Override
	public void process(Page page) {
		Long av = Long.valueOf(page.getUrl().regex("aid=([^&]+)").get());
		Long taskId = Long.valueOf(page.getUrl().regex("_id_=([^&]+)").get());
		BaseView<List<TagView>> data = JSONObject.parseObject(page.getJson().get(), new TypeReference<BaseView<List<TagView>>>() {});
		if (Objects.equals(data.code, -412)) {
			log.error("被风控: {}", data);
			qqUtil.sendRiskError(this.getClass());
			try {
				Thread.sleep(waitTime * 60 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			page.addTargetRequest(page.getUrl().get());
			page.setSkip(true);
		}else {
			page.putField("av", av);
			page.putField("taskId", taskId);
			page.putField("data", data);
		}
	}

	@Override
	public Site getSite() {
		return Site.me().setRetryTimes(20000).setSleepTime(20000).setCharset("UTF-8").setAcceptStatCode(
				Sets.newHashSet(412, 200)
		);
	}

}
