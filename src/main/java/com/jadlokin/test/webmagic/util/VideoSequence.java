package com.jadlokin.test.webmagic.util;

import com.jadlokin.test.webmagic.mapper.VideoMapper;
import com.jadlokin.test.webmagic.mapper.VideoTagMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Component
public class VideoSequence {

//	private List<Long> avList;
	private Integer index = 0;
	private List<Integer> tagIdList = Arrays.asList(937, 2493141, 2485355);

//	@Autowired
//	public VideoSequence(VideoMapper videoMapper) {
//		this.avList = videoMapper.selectAllAvThatNoInfo();
//	}

	public String lastVideo() {
		if (hasLast()) {
			return "https://api.bilibili.com/x/tag/detail?ps=1"
					+ "&tag_id=" + tagIdList.get(index / 1000)
					+ "&time=" + Instant.now()
					+ "&pn=" + ((index++) % 1000 + 1);
		}else {
			return null;
		}
	}

	public boolean hasLast() {
//		return index < avList.size();
		return index <= 1000;
	}

}
