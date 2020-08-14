package com.jadlokin.test.webmagic.mapper;

import com.jadlokin.test.webmagic.entity.VideoPage;
import org.apache.ibatis.annotations.Param;

public interface VideoPageBaseMapper {
    int deleteByPrimaryKey(@Param("cid") Long cid, @Param("av") Long av);

    int insert(VideoPage record);

    int insertSelective(VideoPage record);

    VideoPage selectByPrimaryKey(@Param("cid") Long cid, @Param("av") Long av);

    int updateByPrimaryKeySelective(VideoPage record);

    int updateByPrimaryKey(VideoPage record);
}