package com.jadlokin.test.webmagic.mapper;

import com.jadlokin.test.webmagic.entity.VideoInfo;

public interface VideoInfoBaseMapper {
    int deleteByPrimaryKey(Long av);

    int insert(VideoInfo record);

    int insertSelective(VideoInfo record);

    VideoInfo selectByPrimaryKey(Long av);

    int updateByPrimaryKeySelective(VideoInfo record);

    int updateByPrimaryKey(VideoInfo record);
}