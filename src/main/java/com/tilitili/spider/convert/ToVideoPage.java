package com.tilitili.spider.convert;

import com.tilitili.common.entity.VideoPage;
import com.tilitili.common.entity.view.view.PageView;
import com.tilitili.common.entity.view.view.VideoView;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ToVideoPage {
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "av", source = "videoView.aid")
    @Mapping(target = "cid", source = "pageView.cid")
    @Mapping(target = "page", source = "pageView.page")
    @Mapping(target = "from", source = "pageView.from")
    @Mapping(target = "part", source = "pageView.part")
    @Mapping(target = "duration", source = "pageView.duration")
    @Mapping(target = "vid", source = "pageView.vid")
    @Mapping(target = "weblink", source = "pageView.weblink")
    VideoPage fromVideoView(PageView pageView, VideoView videoView);
}

