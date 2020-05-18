package com.jadlokin.test.webmagic.component.pageList;

import com.alibaba.fastjson.JSONObject;
import com.jadlokin.test.webmagic.entity.VideoInfo;
import com.jadlokin.test.webmagic.entity.VideoPage;
import com.jadlokin.test.webmagic.mapper.VideoInfoMapper;
import com.jadlokin.test.webmagic.mapper.VideoPageMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
			if (videoPageMapper.selectByPrimaryKey(videoPage.getCid(), videoPage.getAv()) == null) {
				videoPageMapper.insertSelective(videoPage);
			}else {
				videoPageMapper.updateByPrimaryKeySelective(videoPage);
			}
		});
	}
}
