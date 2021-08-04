package com.tilitili.spider.convert;

import com.tilitili.common.entity.VideoInfo;
import com.tilitili.common.entity.view.view.VideoView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public interface ToVideoInfo {

    @Mapping(target = "bv", source = "bvid")
    @Mapping(target = "av", source = "aid")
    @Mapping(target = "type", source = "tname")
    @Mapping(target = "copyright", expression = "java(videoView.copyright - 1 == 1)")
    @Mapping(target = "img", source = "pic")
    @Mapping(target = "name", source = "title")
    @Mapping(target = "pubTime", expression = "java(pubTimeConvert(videoView.pubdate))")
    @Mapping(target = "description", source = "desc")
    @Mapping(target = "state", source = "state")
    @Mapping(target = "attribute", source = "attribute")
    @Mapping(target = "duration", source = "duration")
    @Mapping(target = "dynamic", source = "dynamic")
    @Mapping(target = "owner", source = "videoView.owner.name")
    VideoInfo fromVideoView(VideoView videoView);

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Shanghai"));
    default String pubTimeConvert(Long time) {
        return formatter.format(Instant.ofEpochSecond(time));
    }

}
