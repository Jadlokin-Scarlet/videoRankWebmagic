package com.jadlokin.test.webmagic.mapper;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface VideoTagMapper extends  VideoTagBaseMapper{

    List<Long> selectAllForAv();

    List<Long> selectNoInfoVideoForAv();
}