package com.tilitili.spider.util;

import com.tilitili.common.entity.VideoInfo;
import com.tilitili.spider.view.view.VideoView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring", imports = java.time.Instant.class, uses = Convert.class)
public interface Convert {

    @Mapping(target = "bv", source = "bvid")
    @Mapping(target = "av", source = "aid")
    @Mapping(target = "type", source = "tname")
    @Mapping(target = "copyright", expression = "java(videoView.copyright - 1 == 1)")
    @Mapping(target = "pubTime", source = "pubdate")//expression = "java(formatter.format(Instant.ofEpochSecond(videoView.pubdate)))")
    @Mapping(target = "owner", source = "videoView.owner.name")
    VideoInfo VideoViewToVideoInfo(VideoView videoView);

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());
    default String mapPubTime(Integer pubdate) {
        return formatter.format(Instant.ofEpochSecond(pubdate));
    }

}
