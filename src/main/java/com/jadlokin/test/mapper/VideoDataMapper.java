package com.jadlokin.test.mapper;

import com.jadlokin.test.entity.VideoData;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface VideoDataMapper extends VideoDataBaseMapper{
    List<VideoData> selectAllForPage();
    List<VideoData> selectAll(short issue);
    List<VideoData> selectAllForIssue();
    int updateAllIssue(VideoData videoData);
    List<VideoData> selectAllDisparityBetweenTwoIssue(short issue);
}