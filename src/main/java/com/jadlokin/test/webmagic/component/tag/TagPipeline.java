package com.jadlokin.test.webmagic.component.tag;

import com.alibaba.fastjson.JSONObject;
import com.jadlokin.test.webmagic.entity.VideoInfo;
import com.jadlokin.test.webmagic.mapper.VideoInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class TagPipeline implements Pipeline {

	private VideoInfoMapper videoInfoMapper;

	@Autowired
	public TagPipeline(VideoInfoMapper videoInfoMapper) {
		this.videoInfoMapper = videoInfoMapper;
	}

	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
			.withZone(ZoneId.systemDefault());

	@Override
	public void process(ResultItems resultItems, Task task) {
		JSONObject data = resultItems.get("data");
		if(data == null) {
			return;
		}
		VideoInfo videoInfo = new VideoInfo()
				.setAv(data.getLong("aid"))
				.setBv(data.getString("bvid"))
				.setName(data.getString("title"))
				.setImg(data.getString("pic"))
				.setType(data.getString("tname"))
				.setOwner(data.getJSONObject("owner").getString("name"))
				.setCopyright(data.getInteger("copyright") - 1 == 1)
				.setPubTime(formatter.format(Instant.ofEpochSecond(data.getLong("pubdate"))));

		if(videoInfoMapper.selectByPrimaryKey(videoInfo.getAv()) == null) {
			videoInfoMapper.insertSelective(videoInfo);
			log.warn("find new video " + videoInfo.getAv());
		}
		videoInfoMapper.updateByPrimaryKeySelective(videoInfo);
	}
}
