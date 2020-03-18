package com.jadlokin.test.util;

import com.jadlokin.test.mapper.VideoTagMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VideoSequence {

	private List<Long> avList;
	private Integer index = 0;

	@Autowired
	public VideoSequence(VideoTagMapper videoTagMapper) {
		this.avList = videoTagMapper.selectAllForAv();
	}

	public String lastVideo() {
		if (hasLast()) {
			return "https://api.bilibili.com/x/web-interface/view?aid=" + avList.get(index++);
		}else {
			return null;
		}
	}

	public boolean hasLast() {
		return index < avList.size();
	}

}
