package com.jadlokin.test.mapper;

import com.jadlokin.test.entity.VideoTag;

public interface VideoTagBaseMapper {
    int deleteByPrimaryKey(Long av);

    int insert(VideoTag record);

    int insertSelective(VideoTag record);

    VideoTag selectByPrimaryKey(Long av);

    int updateByPrimaryKeySelective(VideoTag record);

    int updateByPrimaryKey(VideoTag record);
}