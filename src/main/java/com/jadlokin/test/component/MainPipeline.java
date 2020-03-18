package com.jadlokin.test.component;

import com.jadlokin.test.entity.VideoInfo;
import com.jadlokin.test.mapper.VideoInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

@Component
public class MainPipeline implements Pipeline {

	private VideoInfoMapper videoInfoMapper;

	@Autowired
	public MainPipeline(VideoInfoMapper videoInfoMapper) {
		this.videoInfoMapper = videoInfoMapper;
	}

	@Override
	public void process(ResultItems resultItems, Task task) {
		VideoInfo videoInfo = resultItems.get("videoInfo");
		if(videoInfo != null) {
			videoInfoMapper.insertSelective(videoInfo);
		}
	}
}
