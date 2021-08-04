package com.tilitili.spider.convert;

import com.tilitili.common.entity.Owner;
import com.tilitili.common.entity.TmpDataNew;
import com.tilitili.common.entity.view.view.VideoView;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = {java.util.Date.class})
public interface ToTmpDataNew {
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "av", source = "aid")
    @Mapping(target = "bv", source = "bvid")
    @Mapping(target = "view", source = "stat.view")
    @Mapping(target = "danmaku", source = "stat.danmaku")
    @Mapping(target = "reply", source = "stat.reply")
    @Mapping(target = "favorite", source = "stat.favorite")
    @Mapping(target = "coin", source = "stat.coin")
    @Mapping(target = "share", source = "stat.share")
    @Mapping(target = "like", source = "stat.like")
    @Mapping(target = "dislike", source = "stat.dislike")
    @Mapping(target = "page", expression = "java((long)videoView.pages.size())")
    @Mapping(target = "copyright", source = "copyright")
    @Mapping(target = "collectTime", expression = "java(new Date())")
    TmpDataNew fromVideoView(VideoView videoView);
}
