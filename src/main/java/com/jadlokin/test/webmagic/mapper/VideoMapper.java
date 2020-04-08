package com.jadlokin.test.webmagic.mapper;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface VideoMapper{

    List<Long> selectAllAvThatNoInfo();

}