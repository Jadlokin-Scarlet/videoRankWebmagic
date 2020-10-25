package com.tilitili.spider.component.pageList;

import com.tilitili.common.entity.VideoInfo;
import com.tilitili.common.entity.VideoPage;
import com.tilitili.common.mapper.VideoInfoMapper;
import com.tilitili.common.mapper.VideoPageMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.stream.Stream;

@Slf4j
@Component
public class PageListPipeline implements Pipeline {

	private VideoPageMapper videoPageMapper;

	@Autowired
	public PageListPipeline(VideoPageMapper videoPageMapper) {
		this.videoPageMapper = videoPageMapper;
	}

	@Override
	public void process(ResultItems resultItems, Task task) {
		Stream<VideoPage> videoPageStream = resultItems.get("videoPageStream");
		if(videoPageStream == null) {
			return;
		}
		videoPageStream.forEach(videoPage -> {
			if (videoPageMapper.getByCidAndAv(videoPage.getCid(), videoPage.getAv()) == null) {
				videoPageMapper.insert(videoPage);
			}else {
				videoPageMapper.update(videoPage);
			}
		});
	}
}
