package com.tilitili.spider.component.owner;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Sets;
import com.tilitili.spider.util.QQUtil;
import com.tilitili.common.entity.view.BaseView;
import com.tilitili.common.entity.view.owner.OwnerView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.Objects;

@Slf4j
@Component
public class OwnerPageProcessor implements PageProcessor {
	private final QQUtil qqUtil;

	@Value("${spider.wait-time}")
	private Integer waitTime;

	@Autowired
	public OwnerPageProcessor(QQUtil qqUtil) {
		this.qqUtil = qqUtil;
	}

	@Override
	public void process(Page page) {
		Long uid = Long.valueOf(page.getUrl().regex("mid=([^&]+)").get());
		Long taskId = Long.valueOf(page.getUrl().regex("_id_=([^&]+)").get());
		BaseView<OwnerView> data = JSONObject.parseObject(page.getJson().get(), new TypeReference<BaseView<OwnerView>>() {});
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
			page.putField("uid", uid);
			page.putField("taskId", taskId);
			page.putField("data", data);
		}
	}

	@Override
	public Site getSite() {
		return Site.me().setRetryTimes(10000).setSleepTime(10000).setCharset("UTF-8").setAcceptStatCode(
				Sets.newHashSet(412, 200)
		);
	}

}
