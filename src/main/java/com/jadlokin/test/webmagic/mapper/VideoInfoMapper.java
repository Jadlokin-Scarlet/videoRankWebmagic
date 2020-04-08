package com.jadlokin.test.webmagic.mapper;

import com.jadlokin.test.webmagic.entity.VideoInfo;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface VideoInfoMapper extends VideoInfoBaseMapper {

	List<VideoInfo> selectAll();
}
