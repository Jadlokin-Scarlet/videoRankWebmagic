package com.jadlokin.test.mapper;

import com.jadlokin.test.entity.VideoTag;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface VideoTagMapper extends  VideoTagBaseMapper{

    List<Long> selectAllForAv();
}