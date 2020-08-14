package com.jadlokin.test.webmagic.mapper;

import com.jadlokin.test.webmagic.entity.VideoData;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface VideoDataMapper extends VideoDataBaseMapper{
    List<VideoData> selectAllForPage();
    List<VideoData> selectAll(short issue);
    List<VideoData> selectAllForIssue();
    int updateAllIssue(VideoData videoData);
    List<VideoData> selectAllDisparityBetweenTwoIssue(short issue);

    @Select("select max(issue) from video_data")
    int getNewIssue();
}