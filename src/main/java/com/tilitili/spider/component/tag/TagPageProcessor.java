package com.tilitili.spider.component.tag;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Sets;
import com.tilitili.common.utils.Log;
import com.tilitili.spider.view.BaseView;
import com.tilitili.spider.view.tag.TagView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class TagPageProcessor implements PageProcessor {

//	private VideoInfoMapper videoInfoMapper;

	@Override
	public void process(Page page) {
		Long av = Long.valueOf(page.getUrl().regex("aid=([^&]+)").get());
		Long taskId = Long.valueOf(page.getUrl().regex("_id_=([^&]+)").get());
		BaseView<List<TagView>> data = JSONObject.parseObject(page.getJson().get(), new TypeReference<BaseView<List<TagView>>>() {});
		if (Objects.equals(data.code, -412)) {
			Log.error("被风控: ", data);
			try {
				Thread.sleep(20 * 60 * 1000);
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
