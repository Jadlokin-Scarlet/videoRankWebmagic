package com.tilitili.spider.convert;

import com.tilitili.common.entity.Owner;
import com.tilitili.common.entity.view.view.VideoView;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ToOwner {
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "uid", source = "owner.mid")
    @Mapping(target = "name", source = "owner.name")
    @Mapping(target = "face", source = "owner.face")
    Owner fromVideoView(VideoView videoView);
}
